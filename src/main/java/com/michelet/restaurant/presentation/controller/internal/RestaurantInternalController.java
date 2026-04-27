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

    /**
     * 내부 식당 단건 조회 API
     *
     * restaurantId를 기준으로 식당 정보를 조회
     * 내부 API이므로 ownerId를 포함한 상세 정보를 반환
     */
    @GetMapping("/{restaurantId}")
    public ApiResponse<GetInternalRestaurantResponse> getRestaurant(@PathVariable UUID restaurantId) {
        GetRestaurantResult result = restaurantQueryService.getRestaurant(restaurantId);

        return ApiResponse.ok(GetInternalRestaurantResponse.from(result));
    }
}
