package com.example.CargoFlow.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisDemoService {

    private final RedisConnectionFactory redisConnectionFactory;
    private final StringRedisTemplate redisTemplate;

    public String ping() {
        try (var connection = redisConnectionFactory.getConnection()) {
            return connection.ping();
        }
    }

    public void saveDemoValue() {
        redisTemplate.opsForValue().set(
                "demo:message",
                "hello from spring",
                Duration.ofSeconds(60)
        );
    }

    public String getDemoValue() {
        return redisTemplate.opsForValue().get("demo:message");
    }

    public Long getDemoTtl() {
        return redisTemplate.getExpire("demo:message");
    }

    @Cacheable(cacheNames = "demoPing", key = "'response'")
    public String getCachedPingStatus() {
        log.info("PING METHOD REALLY EXECUTED");

        return "ok";
    }

    @CacheEvict(cacheNames = "demoPing", key = "'response'")
    public void clearPingCache() {
        log.info("PING CACHE EVICTED");
    }

    @CachePut(cacheNames = "demoPing", key = "'response'")
    public String updatePingCache() {
        log.info("PING CACHE UPDATED");
        return "updated";
    }
}