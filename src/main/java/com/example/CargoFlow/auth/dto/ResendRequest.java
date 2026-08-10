package com.example.CargoFlow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResendRequest {
    @Email(message = "incorrect email format")
    @Size(max = 254, message = "Email max length is 254")
    @NotBlank(message = "email is blank")
    private String email;
}
