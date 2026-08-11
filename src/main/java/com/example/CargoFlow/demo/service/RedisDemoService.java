package com.example.CargoFlow.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
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
}