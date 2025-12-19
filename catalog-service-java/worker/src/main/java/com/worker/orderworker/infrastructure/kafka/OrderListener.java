package com.worker.orderworker.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worker.orderworker.application.OrderUseCase;
import com.worker.orderworker.domain.model.OrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
    private static final Logger log = LoggerFactory.getLogger(OrderListener.class);

    @Autowired
    private OrderUseCase useCase;

    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "orders-topic", groupId = "grooup_id")
    public void consume(String payload){
        log.info("Received raw payload from Kafka: {}", payload);
        try {
            OrderMessage message = mapper.readValue(payload, OrderMessage.class);
            useCase.processOrder(message)
                    .doOnError(err -> log.error("Error processing order {}", message.getOrderId(), err))
                    .doOnSuccess(v -> log.info("Processing completed for order {}", message.getOrderId()))
                    .subscribe();
        } catch (Exception ex){
            log.error("Failed to parse incoming message: {}", ex.getMessage());
        }
    }
}
