package com.example.CargoFlow.auth.dto;

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
public class TokenPairByRefreshTokenRequest {

    @NotBlank(message = "Refresh token is not blank")
    @Size(max = 36, message = "Refresh token length is 36")
    private String refreshToken;

}
