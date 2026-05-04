package com.michelet.restaurantservice.restaurant.presentation.controller.external;

import com.michelet.common.auth.core.annotation.RequireRole;
import com.michelet.common.auth.core.enums.UserRole;
import com.michelet.common.auth.webmvc.context.UserContextHolder;
import com.michelet.common.response.ApiResponse;
import com.michelet.restaurantservice.restaurant.application.command.CreateRestaurantCommand;
import com.michelet.restaurantservice.restaurant.application.query.RestaurantSearchCondition;
import com.michelet.restaurantservice.restaurant.application.result.CreateRestaurantResult;
import com.michelet.restaurantservice.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurantservice.restaurant.application.result.RestaurantSummaryResult;
import com.michelet.restaurantservice.restaurant.application.service.RestaurantCommandService;
import com.michelet.restaurantservice.restaurant.application.service.RestaurantQueryService;
import com.michelet.restaurantservice.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurantservice.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurantservice.restaurant.domain.model.RestaurantStatus;
import com.michelet.restaurantservice.restaurant.presentation.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
     * 식당 등록
     * 식당 등록은 OWNER 권한을 가진 사용자만 수행
     */
    @RequireRole(UserRole.OWNER)
    @PostMapping
    public ApiResponse<CreateRestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {

        UUID ownerId = getAuthenticatedUserId();

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
     * 식당 상세 조회 API
     * 사용자가 식당 상세 정보를 조회할 때 사용하는 외부 API
     * ownerId 같은 내부 식별 정보는 응답에 포함x
     */
    @RequireRole({UserRole.USER, UserRole.OWNER, UserRole.MASTER})
    @GetMapping("/{restaurantId}")
    public ApiResponse<GetRestaurantResponse> getRestaurant(@PathVariable UUID restaurantId) {

        GetRestaurantResult result = restaurantQueryService.getRestaurant(restaurantId);

        return ApiResponse.ok(GetRestaurantResponse.from(result));
    }

    /**
     * 식당 목록/검색 조회 API
     * Pageable 기본값은 createdAt desc, size 10으로 설정
     */
    @RequireRole({UserRole.USER, UserRole.OWNER, UserRole.MASTER})
    @GetMapping
    public ApiResponse<PageResponse<RestaurantSummaryResponse>> getRestaurants(@RequestParam(required = false) String keyword,
                                                                               @RequestParam(required = false) String region,
                                                                               @RequestParam(required = false) RestaurantStatus status,
                                                                               @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        RestaurantSearchCondition condition = new RestaurantSearchCondition(
                keyword,
                region,
                status
        );

        Page<RestaurantSummaryResult> resultPage = restaurantQueryService.getRestaurants(
                condition,
                pageable
        );

        Page<RestaurantSummaryResponse> responsePage =
                resultPage.map(result -> RestaurantSummaryResponse.from(result));

        return ApiResponse.ok(PageResponse.from(responsePage));
    }

    private UUID getAuthenticatedUserId() {
        String userId = UserContextHolder.get().userId();

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException exception) {
            throw new RestaurantException(RestaurantErrorCode.RESTAURANT_401_INVALID_AUTH_USER_ID);
        }
    }
}
