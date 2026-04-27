package com.michelet.restaurant.presentation;

import com.michelet.restaurant.application.service.query.RestaurantQueryService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class HealthController {

    private final RestaurantQueryService restaurantQueryService;

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "success", true,
                "data", restaurantQueryService.getHealthStatus(),
                "message", "Restaurant Query Service is running"
        );
    }
}