package com.example.CargoFlow.auth.dto;

import com.example.CargoFlow.users.entity.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "email is blank")
    private String email;

    @NotBlank(message = "password is blank")
    private String password;

    @NotBlank(message = "full name is blank")
    private String fullName;

    private UserRole role;

}