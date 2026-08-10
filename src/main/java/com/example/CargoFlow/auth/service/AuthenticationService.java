package com.example.CargoFlow.auth.service;

import com.example.CargoFlow.auth.dto.*;
import com.example.CargoFlow.auth.entity.RefreshTokenEntity;
import com.example.CargoFlow.exception.RefreshTokenException;
import com.example.CargoFlow.exception.UserAlreadyExistsException;
import com.example.CargoFlow.exception.UserNotFoundException;
import com.example.CargoFlow.users.dto.response.UserResponse;
import com.example.CargoFlow.users.entity.UserEntity;
import com.example.CargoFlow.users.entity.enums.UserRole;
import com.example.CargoFlow.users.entity.enums.UserStatus;
import com.example.CargoFlow.users.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-token.expiration.ms}")
    private long accessTokenExpirationMs;

    public long getAccessExpiresInSeconds() {
        return accessTokenExpirationMs / 1000;
    }

    @Value("${jwt.refresh-token.expiration.sec}")
    private long refreshTokenExpirationSec;

    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("user with email - " + request.getEmail() + " not found"));

        var refreshToken = refreshTokenService.createRefreshToken(userEntity).getRefreshToken();

        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(userEntity))
                .accessExpiresIn((int) getAccessExpiresInSeconds())
                .refreshToken(refreshToken)
                .refreshExpiresIn((int) refreshTokenExpirationSec)
                .user(toUserResponse(userEntity))
                .build();
    }

    @Transactional
    public RegistrationResponse register(RegisterRequest request) {

        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(
                    "User with this email already exists"
            );
        }

        UserEntity userEntity = UserEntity.builder()
                .emailVerified(true)///////////////////////////////////////Заглушка
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(UserRole.valueOf(request.getRole()))
                .status(UserStatus.ACTIVE)
                .phone(normalizePhone(request.getPhone()))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        userRepository.save(userEntity);

        return RegistrationResponse.builder()
                .user(toUserResponse(userEntity))
                .emailVerificationRequired(true)////////Заглушка
                .verificationExpiresIn(0)///////////////Заглушка
                .build();
    }

    @Transactional
    public TokenPairResponse refresh(TokenPairByRefreshTokenRequest request) {
        RefreshTokenEntity oldToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));

        UserEntity userEntity = oldToken.getUser();

        var accessToken = jwtService.generateToken(userEntity);
        var refreshToken = refreshTokenService.rotateRefreshToken(oldToken).getRefreshToken();

        return TokenPairResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void logout( TokenPairByRefreshTokenRequest request) {
        refreshTokenService.deleteRefreshToken(request.getRefreshToken());
    }
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        return phone.trim();
    }

    private UserResponse toUserResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.isEmailVerified(),
                user.getAvatarFileId(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }


}