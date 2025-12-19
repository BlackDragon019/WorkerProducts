package com.worker.orderworker.application;

import com.worker.orderworker.domain.model.OrderMessage;
import com.worker.orderworker.domain.model.ProcessedOrder;
import com.worker.orderworker.domain.model.ProductDTO;
import com.worker.orderworker.domain.repository.OrderMongoRepository;
import com.worker.orderworker.infrastructure.client.CatalogClient;
import com.worker.orderworker.infrastructure.redis.RedisLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
public class OrderUseCase {
    private static final Logger log = LoggerFactory.getLogger(OrderUseCase.class);

    @Autowired
    RedisLockService lockService;

    @Autowired
    CatalogClient catalogClient;

    @Autowired
    OrderMongoRepository repository;

    @Autowired
    com.worker.orderworker.infrastructure.redis.FailedMessageService failedMessageService;

    public Mono<Void> processOrder(OrderMessage message){
        String lockKey = "lock:order:" + message.getOrderId();

        return lockService.tryLock(lockKey)
                .flatMap(acquired -> {
                    if (!acquired) return Mono.empty();

                    // First validate customer
                    return catalogClient.getCustomer(message.getCustomerId())
                            .flatMap(customer -> {
                                if (customer.getActive() == null || !customer.getActive()){
                                    return handleFailure(message);
                                }

                                return Flux.fromIterable(message.getProductIds())
                                        .flatMap(id -> catalogClient.getProduct(id))
                                        .collectList()
                                        .flatMap(products -> {
                                            // Validate that all products were found
                                            if (products == null || products.size() != message.getProductIds().size()){
                                                return handleFailure(message);
                                            }

                                            return Mono.fromCallable(() -> {
                                                ProcessedOrder order = new ProcessedOrder();
                                                order.setOrderId(message.getOrderId());
                                                order.setCustomerId(message.getCustomerId());
                                                order.setProducts(products);
                                                double total = products.stream().mapToDouble(p -> p.getPrice() != null ? p.getPrice() : 0).sum();
                                                order.setTotalAmount(total);
                                                log.info("Saving processed order for orderId={} with {} products", order.getOrderId(), order.getProducts().size());
                                                return repository.save(order);
                                            }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic()).then();
                                        })
                                        .onErrorResume(err -> handleFailure(message));
                            })
                            .onErrorResume(err -> handleFailure(message))
                            .then(lockService.releaseLock(lockKey));
                });
    }

    private Mono<Void> handleFailure(OrderMessage message){
        // Almacenar payload y contar reintentos en Redis
        try {
            String payload = message.toString();
            log.warn("Handling failure for order {}: storing payload and incrementing attempts", message.getOrderId());
            return failedMessageService.storePayloadIfNotExists(message.getOrderId(), payload)
                    .then(failedMessageService.incrementAttempts(message.getOrderId())
                            .flatMap(attempts -> {
                                if (attempts >= failedMessageService.getMaxRetries()){
                                    // Aquí podríamos mover a una cola dead-letter o notificar
                                    log.error("Order {} exceeded max retries: {}", message.getOrderId(), attempts);
                                }
                                return Mono.empty();
                            }))
                    .then();
        } catch (Exception ex){
            log.error("Error storing failed message {}: {}", message.getOrderId(), ex.getMessage());
            return Mono.empty();
        }
    }
}
