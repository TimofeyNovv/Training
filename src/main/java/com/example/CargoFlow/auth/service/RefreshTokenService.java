package com.example.CargoFlow.auth.service;

import com.example.CargoFlow.auth.entity.RefreshTokenEntity;
import com.example.CargoFlow.auth.repository.RefreshTokenRepository;
import com.example.CargoFlow.exception.RefreshTokenException;
import com.example.CargoFlow.users.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token.expiration.sec}")
    private Integer refreshTokenExpirationSeconds;

    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        deleteAllByUser(user);

        return RefreshTokenEntity.builder()
                .createdAt(Instant.now())
                .expiryAt(Instant.now().plusSeconds(refreshTokenExpirationSeconds))
                .refreshToken(UUID.randomUUID().toString())
                .user(user)
                .build();
    }

    @Transactional
    private void deleteAllByUser(UserEntity user) {
        refreshTokenRepository.deleteAllByUser(user);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshTokenEntity> findByToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    @Transactional
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity refreshToken) {
        if (refreshToken.getExpiryAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenException("Refresh token expired");
        }
        return refreshToken;
    }

    @Transactional
    public RefreshTokenEntity rotateRefreshToken(RefreshTokenEntity oldToken) {

        verifyExpiration(oldToken);

        refreshTokenRepository.delete(oldToken);
        return createRefreshToken(oldToken.getUser());
    }
}
