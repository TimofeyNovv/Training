package com.example.CargoFlow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationRequest {

    @Email(message = "incorrect email format")
    @Size(max = 254, message = "Email max length is 254")
    @NotBlank(message = "email is blank")
    private String email;

    @NotBlank(message = "code is blank")
    @Pattern(
            regexp = "^[0-9]{6}$",
            message = "code must contain exactly 6 digits"
    )
    private String code;
}