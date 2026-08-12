package com.example.CargoFlow.auth.service;

import com.example.CargoFlow.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisRateLimitService {

    private static final String VERIFY_ATTEMPTS_PREFIX = "rate:email-verify:attempts:";
    private static final String VERIFY_BLOCK_PREFIX = "rate:email-verify:block:";
    private static final RedisScript<Long> INCREMENT_WITH_TTL =
            RedisScript.of("""
                    local current = redis.call('INCR', KEYS[1])
                    
                    if current == 1 then
                        redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1]))
                    end
                    
                    return current
                    """, Long.class);
    private static final RedisScript<Long> VERIFY_FAILURE_SCRIPT =
            RedisScript.of("""
                    local current = redis.call('INCR', KEYS[1])
                    
                    if current == 1 then
                        redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1]))
                    end
                    
                    if current >= tonumber(ARGV[2]) then
                        redis.call(
                            'SET',
                            KEYS[2],
                            '1',
                            'EX',
                            tonumber(ARGV[1])
                        )
                    end
                    
                    return current
                    """, Long.class);

    private final StringRedisTemplate redisTemplate;

    public void checkAndConsume(
            String key,
            int limit,
            Duration window
    ) {
        Long current = redisTemplate.execute(
                INCREMENT_WITH_TTL,
                List.of(key),
                String.valueOf(window.toSeconds())
        );

        if (current != null && current > limit) throw new RateLimitExceededException("Too many requests");
    }

    public boolean isVerificationBlocked(UUID userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(VERIFY_BLOCK_PREFIX + userId));
    }

    public void recordVerificationFailure(UUID userId) {

        String attemptsKey =
                VERIFY_ATTEMPTS_PREFIX + userId;
        String blockKey =
                VERIFY_BLOCK_PREFIX + userId;

        redisTemplate.execute(
                VERIFY_FAILURE_SCRIPT,
                List.of(attemptsKey, blockKey),
                String.valueOf(Duration.ofMinutes(15).toSeconds()),
                "5"
        );
    }

    public void clearVerificationFailures(UUID userId) {
        redisTemplate.delete(
                List.of(
                        VERIFY_ATTEMPTS_PREFIX + userId,
                        VERIFY_BLOCK_PREFIX + userId
                )
        );
    }
}