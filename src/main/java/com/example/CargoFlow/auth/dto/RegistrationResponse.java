package com.example.CargoFlow.auth.dto;

import com.example.CargoFlow.users.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {
    UserResponse user;
    boolean emailVerificationRequired;
    long verificationExpiresIn;
}