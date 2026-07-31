package com.example.CargoFlow.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

//    @Email(message = "incorrect email format")
//    @Size(max = 254, message = "Email max length is 255")
    @NotBlank(message = "email is blank")
    private String email;

//    @Pattern(
//            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
//            message = "password must contain at least one letter and one digit"
//    )
//    @Size(min = 8, max = 72, message = "password length min - 8, max - 72")
    @NotBlank(message = "password is blank")
    private String password;

}