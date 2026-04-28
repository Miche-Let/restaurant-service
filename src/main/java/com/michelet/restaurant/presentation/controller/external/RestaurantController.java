package com.michelet.restaurant.presentation.controller.external;

import com.michelet.common.response.ApiResponse;
import com.michelet.restaurant.application.command.CreateRestaurantCommand;
import com.michelet.restaurant.application.query.RestaurantSearchCondition;
import com.michelet.restaurant.application.result.CreateRestaurantResult;
import com.michelet.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurant.application.result.RestaurantSummaryResult;
import com.michelet.restaurant.application.service.command.RestaurantCommandService;
import com.michelet.restaurant.application.service.query.RestaurantQueryService;
import com.michelet.restaurant.domain.model.RestaurantStatus;
import com.michelet.restaurant.presentation.dto.CreateRestaurantRequest;
import com.michelet.restaurant.presentation.dto.CreateRestaurantResponse;
import com.michelet.restaurant.presentation.dto.GetRestaurantResponse;
import com.michelet.restaurant.presentation.dto.RestaurantSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantCommandService restaurantCommandService;
    private final RestaurantQueryService restaurantQueryService;

    public RestaurantController(RestaurantCommandService restaurantCommandService, RestaurantQueryService restaurantQueryService) {
        this.restaurantCommandService = restaurantCommandService;
        this.restaurantQueryService = restaurantQueryService;
    }

    /**
     * 식당 등록 API
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

    /**
     * 외부 식당 상세 조회 API
     *
     * 사용자가 식당 상세 정보를 조회할 때 사용하는 외부 API
     * ownerId 같은 내부 식별 정보는 응답에 포함x
     */
    @GetMapping("/{restaurantId}")
    public ApiResponse<GetRestaurantResponse> getRestaurant(@PathVariable UUID restaurantId) {

        GetRestaurantResult result = restaurantQueryService.getRestaurant(restaurantId);

        return ApiResponse.ok(GetRestaurantResponse.from(result));
    }

    /**
     * 외부 식당 목록/검색 조회 API
     *
     * name, status 조건으로 식당 목록을 검색
     * 페이징 정보는 Pageable을 사용
     */
    @GetMapping
    public ApiResponse<Page<RestaurantSummaryResponse>> getRestaurants(@RequestParam(required = false) String name,
                                                                       @RequestParam(required = false) RestaurantStatus status,
                                                                       Pageable pageable) {

        RestaurantSearchCondition condition = new RestaurantSearchCondition(name, status);

        Page<RestaurantSummaryResult> resultPage = restaurantQueryService.getRestaurants(condition, pageable);

        Page<RestaurantSummaryResponse> responsePage =
                resultPage.map(result -> RestaurantSummaryResponse.from(result));

        return ApiResponse.ok(responsePage);
    }
}
