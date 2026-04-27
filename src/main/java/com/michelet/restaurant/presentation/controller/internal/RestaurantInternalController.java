package com.michelet.restaurant.presentation.controller.internal;

import com.michelet.common.response.ApiResponse;
import com.michelet.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurant.application.service.query.RestaurantQueryService;
import com.michelet.restaurant.presentation.dto.GetInternalRestaurantResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/restaurants")
public class RestaurantInternalController {

    private final RestaurantQueryService restaurantQueryService;

    public RestaurantInternalController(RestaurantQueryService restaurantQueryService) {
        this.restaurantQueryService = restaurantQueryService;
    }

    @GetMapping("/{restaurantId}")
    public ApiResponse<GetInternalRestaurantResponse> getRestaurant(@PathVariable UUID restaurantId) {
        GetRestaurantResult result = restaurantQueryService.getRestaurant(restaurantId);

        return ApiResponse.ok(GetInternalRestaurantResponse.from(result));
    }
}
