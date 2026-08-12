package com.example.CargoFlow.exception;

public class InvalidEmailVerificationCodeException extends RuntimeException {
    public InvalidEmailVerificationCodeException(String message) {
        super(message);
    }
}
