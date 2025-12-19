package com.worker.orderworker.infrastructure.client;

import com.worker.orderworker.domain.model.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class CatalogClient {
    private final WebClient webClient;

    public CatalogClient(WebClient.Builder builder, @Value("${catalog.api.url}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }

    public Mono<ProductDTO> getProduct(String id) {
        return webClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof WebClientResponseException))
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<com.worker.orderworker.domain.model.CustomerDTO> getCustomer(String id) {
        return webClient.get()
                .uri("/customers/{id}", id)
                .retrieve()
                .bodyToMono(com.worker.orderworker.domain.model.CustomerDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof WebClientResponseException))
                .onErrorResume(e -> Mono.empty());
    }
}
