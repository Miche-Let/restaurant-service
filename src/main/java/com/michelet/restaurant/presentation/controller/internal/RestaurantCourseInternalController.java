package com.michelet.restaurant.presentation.controller.internal;

import com.michelet.common.response.ApiResponse;
import com.michelet.restaurant.application.result.CourseSummaryResult;
import com.michelet.restaurant.application.service.query.RestaurantCourseQueryService;
import com.michelet.restaurant.presentation.dto.CourseSummaryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/restaurants/{restaurantId}/courses")
public class RestaurantCourseInternalController {

    private final RestaurantCourseQueryService restaurantCourseQueryService;

    public RestaurantCourseInternalController(RestaurantCourseQueryService restaurantCourseQueryService) {
        this.restaurantCourseQueryService = restaurantCourseQueryService;
    }

    // 식당 ID 기준으로 내부 코스 목록을 조회
    @GetMapping
    public ApiResponse<List<CourseSummaryResponse>> getInternalCourses(@PathVariable UUID restaurantId) {

        List<CourseSummaryResult> results = restaurantCourseQueryService.getInternalCourses(restaurantId);

        List<CourseSummaryResponse> responses = results.stream()
                .map(result -> CourseSummaryResponse.from(result))
                .toList();

        return ApiResponse.ok(responses);
    }
}
