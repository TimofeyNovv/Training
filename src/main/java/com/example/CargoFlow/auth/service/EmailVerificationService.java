package com.example.CargoFlow.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final String CODE_KEY_PREFIX = "auth:email-code:";

    private static final Duration CODE_TTL = Duration.ofMinutes(15);
    public long getCodeTtlSeconds() {
        return CODE_TTL.toSeconds();
    }

    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private final SecureRandom secureRandom = new SecureRandom();

    public void createAndSendCode(UUID userId, String email) {

        String code = generateCode();
        String codeHash = passwordEncoder.encode(code);
        String key = buildCodeKey(userId);

        redisTemplate.opsForValue().set(
                key,
                codeHash,
                CODE_TTL
        );

        mailService.sendVerificationCode(email, code);
    }

    private String generateCode() {
        int number = secureRandom.nextInt(1_000_000);
        return String.format("%06d", number);
    }

    private String buildCodeKey(UUID userId) {
        return CODE_KEY_PREFIX + userId;
    }

    public boolean verifyCode(UUID userId, String code) {

        String key = buildCodeKey(userId);
        String storedHash = redisTemplate
                .opsForValue()
                .get(key);

        if (storedHash == null) return false;

        return passwordEncoder.matches(
                code,
                storedHash
        );
    }

    public void deleteCode(UUID userId) {
        redisTemplate.delete(buildCodeKey(userId));
    }
}