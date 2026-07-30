package com.example.CargoFlow.exception;

import com.example.CargoFlow.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(
            UserNotFoundException exception
    ) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code("NOT_FOUND")
                .message(exception.getMessage())
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExistsException(
            UserNotFoundException exception
    ) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code("CONFLICT")
                .message(exception.getMessage())
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
