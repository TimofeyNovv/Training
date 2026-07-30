package com.example.CargoFlow.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldErrorResponse {

    private String field;
    private String issue;
}