package com.example.CargoFlow.demo.controller;

import com.example.CargoFlow.demo.dto.PingResponse;
import com.example.CargoFlow.demo.service.RedisDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
public class DemoController {

    private final RedisDemoService redisDemoService;

    @Operation(
            summary = "ping pong"
    )
    @GetMapping("/ping")
    public ResponseEntity<PingResponse> ping() {
        String status = redisDemoService.getCachedPingStatus();

        return ResponseEntity.ok(
                new PingResponse(status, "1.0.0")
        );
    }

    @PostMapping("/ping/clear-cache")
    public ResponseEntity<Void> clearPingCache() {

        redisDemoService.clearPingCache();

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/ping/update-cache")
    public ResponseEntity<String> updatePingCache() {
        return ResponseEntity.ok(redisDemoService.updatePingCache());
    }

    @SecurityRequirement(name = "jwtAuth")
    @Operation(
            summary = "ping достпуный только после атворизации"
    )
    @GetMapping("/authping")
    public ResponseEntity<PingResponse> authPing() {
        return ResponseEntity.ok(new PingResponse("ok", "1.0.0"));
    }

    @GetMapping("/redis-ping")
    public ResponseEntity<String> redisPing() {
        return ResponseEntity.ok(redisDemoService.ping());
    }

    @PostMapping("/redis-demo")
    public ResponseEntity<Void> saveRedisDemo() {
        redisDemoService.saveDemoValue();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/redis-demo")
    public ResponseEntity<String> getRedisDemo() {
        return ResponseEntity.ok(redisDemoService.getDemoValue());
    }

    @GetMapping("/redis-demo/ttl")
    public ResponseEntity<Long> getRedisDemoTtl() {
        return ResponseEntity.ok(redisDemoService.getDemoTtl());
    }
}
