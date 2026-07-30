package com.example.CargoFlow.auth.service;

import com.example.CargoFlow.auth.dto.AuthenticationRequest;
import com.example.CargoFlow.auth.dto.AuthenticationResponse;
import com.example.CargoFlow.auth.dto.RegisterRequest;
import com.example.CargoFlow.exception.UserAlreadyExistsException;
import com.example.CargoFlow.exception.UserNotFoundException;
import com.example.CargoFlow.users.entity.UserEntity;
import com.example.CargoFlow.users.entity.enums.UserStatus;
import com.example.CargoFlow.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("user with email - " + request.getEmail() + " not found"));

        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(userEntity))
                .build();
    }

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("user with email - " + request.getEmail() + " already exists");
        }

        UserEntity userEntity = UserEntity.builder()
                .email(request.getEmail().trim().toLowerCase(Locale.ROOT))
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .phone(request.getPhone())
                .build();

        userRepository.save(userEntity);

        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(userEntity))
                .build();
    }
}