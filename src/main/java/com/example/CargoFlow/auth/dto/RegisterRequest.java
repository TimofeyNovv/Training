package com.example.CargoFlow.auth.dto;

import com.example.CargoFlow.users.entity.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Email(message = "incorrect email format")
    @Size(max = 254, message = "Email max length is 254")
    @NotBlank(message = "email is blank")
    private String email;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "password must contain at least one letter and one digit"
    )
    @Size(min = 8, max = 72, message = "password length min - 8, max - 72")
    @NotBlank(message = "password is blank")
    private String password;

    @Size(min = 2, max = 120, message = "fullName length min - 2, max - 120")
    @NotBlank(message = "full name is blank")
    private String fullName;

    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone must be in E.164 format")
    private String phone;

    @NotNull(message = "role is null")
    @Pattern(regexp = "^(CUSTOMER|DRIVER)$",
            message = "Role must be one of: CUSTOMER, DRIVER")
    private String role;

}