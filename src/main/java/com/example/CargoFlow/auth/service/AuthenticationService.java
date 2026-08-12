package com.example.CargoFlow.auth.service;

import com.example.CargoFlow.auth.dto.*;
import com.example.CargoFlow.auth.entity.RefreshTokenEntity;
import com.example.CargoFlow.exception.*;
import com.example.CargoFlow.users.dto.response.UserResponse;
import com.example.CargoFlow.users.entity.UserEntity;
import com.example.CargoFlow.users.entity.enums.UserRole;
import com.example.CargoFlow.users.entity.enums.UserStatus;
import com.example.CargoFlow.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import java.time.Duration;
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
    private final EmailVerificationService emailVerificationService;
    private final RedisRateLimitService redisRateLimitService;

    @Value("${jwt.access-token.expiration.ms}")
    private long accessTokenExpirationMs;

    public long getAccessExpiresInSeconds() {
        return accessTokenExpirationMs / 1000;
    }

    @Value("${jwt.refresh-token.expiration.sec}")
    private long refreshTokenExpirationSec;

    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request,  String clientIp) {

        String email = normalizeEmail(request.getEmail());

        String emailHash = hashForRedisKey(email);

        redisRateLimitService.checkAndConsume(
                "rate:login:ip:" + clientIp,
                10,
                Duration.ofMinutes(15)
        );

        redisRateLimitService.checkAndConsume(
                "rate:login:email:" + emailHash,
                5,
                Duration.ofMinutes(15)
        );

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()
                )
        );

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email - " + email + " not found"));

        if (!userEntity.isEmailVerified()) throw new EmailNotVerifiedException("Email is not verified");

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
    public RegistrationResponse register(RegisterRequest request, String clientIp) {

        redisRateLimitService.checkAndConsume(
                "rate:register:ip:" + clientIp,
                5,
                Duration.ofMinutes(15)
        );

        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(
                    "User with this email already exists"
            );
        }

        UserEntity userEntity = UserEntity.builder()
                .emailVerified(false)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(UserRole.valueOf(request.getRole()))
                .status(UserStatus.ACTIVE)
                .phone(normalizePhone(request.getPhone()))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        UserEntity savedUser = userRepository.save(userEntity);
        emailVerificationService.createAndSendCode(
                savedUser.getId(),
                savedUser.getEmail()
        );

        return RegistrationResponse.builder()
                .user(toUserResponse(savedUser))
                .emailVerificationRequired(true)
                .verificationExpiresIn(emailVerificationService.getCodeTtlSeconds())
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
    public void logout(TokenPairByRefreshTokenRequest request) {
        refreshTokenService.deleteRefreshToken(request.getRefreshToken());
    }

    @Transactional
    public UserEntity confirmEmail(EmailVerificationRequest request) {

        String email = normalizeEmail(request.getEmail());

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidEmailVerificationCodeException("Invalid or expired verification code"));

        if (user.isEmailVerified()) throw new EmailAlreadyVerifiedException("Email is already verified");

        if (redisRateLimitService.isVerificationBlocked(user.getId())) {
            throw new RateLimitExceededException("Email verification temporarily blocked");
        }

        boolean codeValid = emailVerificationService.verifyCode(
                user.getId(),
                request.getCode()
        );

        if (!codeValid) {
            redisRateLimitService.recordVerificationFailure(user.getId());
            throw new InvalidEmailVerificationCodeException("Invalid or expired verification code");
        }

        user.setEmailVerified(true);
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);

//        emailVerificationService.deleteCode(
//                user.getId()
//        );

        return user;
    }

    @Transactional
    public AuthenticationResponse verifyEmail(EmailVerificationRequest request) {

        UserEntity user = confirmEmail(request);
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService
                .createRefreshToken(user)
                .getRefreshToken();

        emailVerificationService.deleteCode(
                user.getId()
        );

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .accessExpiresIn((int) getAccessExpiresInSeconds())
                .refreshToken(refreshToken)
                .refreshExpiresIn((int) refreshTokenExpirationSec)
                .user(toUserResponse(user))
                .build();
    }

    public void resendVerificationCode(ResendRequest request, String clientIp) {

        String email = normalizeEmail(request.getEmail());
        String emailHash = hashForRedisKey(email);

        redisRateLimitService.checkAndConsume(
                "rate:email-resend:email:" + emailHash,
                1,
                Duration.ofSeconds(60)
        );

        redisRateLimitService.checkAndConsume(
                "rate:email-resend:ip:" + clientIp,
                5,
                Duration.ofHours(1)
        );

        userRepository.findByEmail(email)
                .filter(user -> !user.isEmailVerified())
                .ifPresent(user ->
                        emailVerificationService.createAndSendCode(
                                user.getId(),
                                user.getEmail()
                        )
                );
    }

        private String hashForRedisKey(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is not available",
                    exception
            );
        }
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