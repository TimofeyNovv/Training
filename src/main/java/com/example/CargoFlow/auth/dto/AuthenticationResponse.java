package com.example.CargoFlow.auth.dto;

import com.example.CargoFlow.users.dto.response.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @Schema(description = "access_token, обрати внимание, что я всего в ответе пишу через нижний слеш")
    private String accessToken;
    private Integer accessExpiresIn;
    private String refreshToken;
    private Integer refreshExpiresIn;
    private UserResponse user;

}