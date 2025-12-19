package com.worker.orderworker.infrastructure.redis;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RedisLockService {
    private final ReactiveStringRedisTemplate redisTemplate;

    public RedisLockService(ReactiveStringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> tryLock(String key){
        return redisTemplate.opsForValue().setIfAbsent(key, "BUSY", Duration.ofMinutes(5));
    }

    public Mono<Void> releaseLock(String key){
        return redisTemplate.delete(key).then();
    }
}
