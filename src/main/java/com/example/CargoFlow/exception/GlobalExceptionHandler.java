package com.example.CargoFlow.exception;

import com.example.CargoFlow.exception.dto.ApiErrorResponse;
import com.example.CargoFlow.exception.dto.FieldErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.LockedException;

import java.time.Instant;
import java.util.List;
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
            UserAlreadyExistsException exception
    ) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code("EMAIL_ALREADY_EXISTS")
                .message(exception.getMessage())
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        List<FieldErrorResponse> fieldErrors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorResponse(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Request validation failed")
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException exception
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message("Invalid email or password")
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException exception
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("MALFORMED_JSON")
                .message("Request body contains invalid JSON")
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleRefreshTokenExpired(RefreshTokenException ex) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message(ex.getMessage())
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(InvalidEmailVerificationCodeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidEmailVerificationCode(
            InvalidEmailVerificationCodeException exception
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message(exception.getMessage())
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyVerified(
            EmailAlreadyVerifiedException exception
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("CONFLICT")
                .message(exception.getMessage())
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimitExceeded(
            RateLimitExceededException exception
    ) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("RATE_LIMIT_EXCEEDED")
                .message(exception.getMessage())
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(response);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailNotVerified(
            EmailNotVerifiedException exception
    ) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("EMAIL_NOT_VERIFIED")
                .message(exception.getMessage())
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiErrorResponse> handleLocked(
            LockedException exception
    ) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("FORBIDDEN")
                .message("User account is blocked")
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }
}
