package com.worker.orderworker.infrastructure.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class FailedMessageService {
    private final ReactiveStringRedisTemplate redisTemplate;

    @Value("${order.max-retries:5}")
    private int maxRetries;

    @Value("${order.failed-ttl-seconds:86400}")
    private long ttlSeconds;

    public FailedMessageService(ReactiveStringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public Mono<Integer> incrementAttempts(String orderId){
        String key = "failed:order:attempts:" + orderId;
        return redisTemplate.opsForValue().increment(key)
                .flatMap(val -> redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds)).thenReturn(val.intValue()))
                .onErrorReturn(0);
    }

    public Mono<Void> storePayloadIfNotExists(String orderId, String payload){
        String key = "failed:order:payload:" + orderId;
        return redisTemplate.opsForValue().setIfAbsent(key, payload, Duration.ofSeconds(ttlSeconds)).then();
    }

    public int getMaxRetries(){
        return maxRetries;
    }
}