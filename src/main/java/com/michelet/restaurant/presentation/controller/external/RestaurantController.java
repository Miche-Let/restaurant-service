package com.michelet.restaurant.presentation.controller.external;

import com.michelet.common.response.ApiResponse;
import com.michelet.restaurant.application.command.CreateRestaurantCommand;
import com.michelet.restaurant.application.result.CreateRestaurantResult;
import com.michelet.restaurant.application.service.command.RestaurantCommandService;
import com.michelet.restaurant.presentation.dto.CreateRestaurantRequest;
import com.michelet.restaurant.presentation.dto.CreateRestaurantResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantCommandService restaurantCommandService;

    public RestaurantController(RestaurantCommandService restaurantCommandService) {
        this.restaurantCommandService = restaurantCommandService;
    }

    /**
     *식당 등록 API
     *
     * 시큐리티 미구현 상태
     * 임시로 X-User-Id 전달받음
     * 추후 변경
     */
    @PostMapping
    public ApiResponse<CreateRestaurantResponse> createRestaurant(@RequestHeader("X-User-Id") UUID ownerId,
                                                                  @Valid @RequestBody CreateRestaurantRequest request) {

        CreateRestaurantCommand command = CreateRestaurantCommand.of(ownerId, request);
        CreateRestaurantResult result = restaurantCommandService.createRestaurant(command);

        return ApiResponse.ok(
                CreateRestaurantResponse.of(
                        result.restaurantId(),
                        result.ownerId()
                )
        );
    }
}
