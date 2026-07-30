package com.example.CargoFlow.auth.controller;

import com.example.CargoFlow.auth.dto.AuthenticationRequest;
import com.example.CargoFlow.auth.dto.AuthenticationResponse;
import com.example.CargoFlow.auth.dto.RegisterRequest;
import com.example.CargoFlow.auth.service.AuthenticationService;
import com.example.CargoFlow.exception.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok().body(authenticationService.register(request));
    }
}