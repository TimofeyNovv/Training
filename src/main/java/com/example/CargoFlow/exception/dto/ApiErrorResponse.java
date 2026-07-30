package com.example.CargoFlow.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiErrorResponse {

    private String code;
    private String message;
    private UUID traceId;
    private Instant timestamp;
    private List<FieldErrorResponse> fieldErrors;
}