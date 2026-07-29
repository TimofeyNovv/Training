package com.example.CargoFlow.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @Operation(
            summary = "ping pong"
    )
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return  ResponseEntity.ok("pong");
    }
}
