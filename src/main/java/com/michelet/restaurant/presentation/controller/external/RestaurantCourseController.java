package com.michelet.restaurant.presentation.controller.external;

import com.michelet.common.response.ApiResponse;
import com.michelet.restaurant.application.command.CreateCourseCommand;
import com.michelet.restaurant.application.result.CourseListItemResult;
import com.michelet.restaurant.application.result.CourseResult;
import com.michelet.restaurant.application.service.command.RestaurantCourseCommandService;
import com.michelet.restaurant.application.service.query.RestaurantCourseQueryService;
import com.michelet.restaurant.presentation.dto.CourseListItemResponse;
import com.michelet.restaurant.presentation.dto.CourseResponse;
import com.michelet.restaurant.presentation.dto.CreateCourseRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/courses")
public class RestaurantCourseController {

    private final RestaurantCourseCommandService restaurantCourseCommandService;
    private final RestaurantCourseQueryService restaurantCourseQueryService;

    public RestaurantCourseController(RestaurantCourseCommandService restaurantCourseCommandService, RestaurantCourseQueryService restaurantCourseQueryService) {
        this.restaurantCourseCommandService = restaurantCourseCommandService;
        this.restaurantCourseQueryService = restaurantCourseQueryService;
    }

    @PostMapping
    public ApiResponse<CourseResponse> createCourse(@PathVariable UUID restaurantId,
                                                    @Valid @RequestBody CreateCourseRequest request) {

        CreateCourseCommand command = CreateCourseCommand.of(restaurantId, request);
        CourseResult result = restaurantCourseCommandService.createCourse(command);

        return ApiResponse.ok(
                CourseResponse.of(
                        result.courseId(),
                        result.restaurantId()
                )
        );
    }

    /**
     * 식당 ID 기준으로 외부 코스 목록을 조회
     * 외부 코스 목록 조회 응답은 코스 요약 정보와 menus[]를 함께 포함
     */
    @GetMapping
    public ApiResponse<List<CourseListItemResponse>> getCourses(@PathVariable UUID restaurantId) {

        List<CourseListItemResult> results = restaurantCourseQueryService.getCourses(restaurantId);

        List<CourseListItemResponse> responses = results.stream()
                .map(result -> CourseListItemResponse.from(result))
                .toList();

        return ApiResponse.ok(responses);
    }
}
