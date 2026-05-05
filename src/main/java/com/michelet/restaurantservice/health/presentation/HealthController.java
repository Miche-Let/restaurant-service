package com.michelet.restaurantservice.health.presentation;

import com.michelet.restaurantservice.health.application.HealthCheckService;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
public class HealthController {

    private final HealthCheckService healthCheckService;

    public HealthController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "success", true,
                "data", healthCheckService.getHealthStatus(),
                "message", "Restaurant Query Service is running"
        );
    }
}