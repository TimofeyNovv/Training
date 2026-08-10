package com.example.CargoFlow.auth.controller;

import com.example.CargoFlow.auth.dto.*;
import com.example.CargoFlow.auth.service.AuthenticationService;
import com.example.CargoFlow.exception.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "эндпоинт для входа",
            responses = {
                    @ApiResponse(responseCode = "200", description = "успешно, возвращается access_token"),
                    @ApiResponse(responseCode = "404", description = "пользователь с таким email не найден", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "некорректный формат json или некорректное заполнение полей json", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok().body(authenticationService.login(request));
    }

    @Operation(
            summary = "эндпоинт для регистрации",
            responses = {
                    @ApiResponse(responseCode = "200", description = "успешно, возвращается access_token"),
                    @ApiResponse(responseCode = "409", description = "пользователь с таким email уже существует", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "некорректный формат json или некорректное заполнение полей json", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED ).body(authenticationService.register(request));
    }

    @Operation(
            summary = "эндпоинт для повторной отправки кода ждя подтверждения почты",
            responses = {
                    @ApiResponse(responseCode = "204", description = "всегда 204")
            }
    )
    @PostMapping("/email/resend")
    public ResponseEntity<?> resend(
            @Valid @RequestBody ResendRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);///нереализованно
    }

    @Operation(
            summary = "получение новой token pair",
            responses = {
                    @ApiResponse(responseCode = "200", description = "успешно, возвращается новая пара токенов"),
                    @ApiResponse(responseCode = "401", description = "некорректный токен")
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<TokenPairResponse> refresh(
            @Valid @RequestBody TokenPairByRefreshTokenRequest request
    ) {
        return ResponseEntity.ok().body(authenticationService.refresh(request));
    }

    @Operation(
            summary = "сделать текущую сессию польщователя недействительной",
            responses = {
                    @ApiResponse(responseCode = "204", description = "при корректном вводе всегда ответ 204")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Valid @RequestBody TokenPairByRefreshTokenRequest request
    ) {
        authenticationService.logout(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}