package com.michelet.restaurantservice.course.presentation.controller.external;

import com.michelet.common.auth.core.annotation.RequireRole;
import com.michelet.common.auth.core.enums.UserRole;
import com.michelet.common.response.ApiResponse;
import com.michelet.restaurantservice.course.application.command.CreateCourseCommand;
import com.michelet.restaurantservice.course.application.result.CourseListItemResult;
import com.michelet.restaurantservice.course.application.result.CourseResult;
import com.michelet.restaurantservice.course.application.service.RestaurantCourseCommandService;
import com.michelet.restaurantservice.course.application.service.RestaurantCourseQueryService;
import com.michelet.restaurantservice.course.presentation.dto.CourseListItemResponse;
import com.michelet.restaurantservice.course.presentation.dto.CourseResponse;
import com.michelet.restaurantservice.course.presentation.dto.CreateCourseRequest;
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

    // 식당 코스등록
    @RequireRole(UserRole.OWNER)
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
    @RequireRole({UserRole.USER, UserRole.OWNER})
    @GetMapping
    public ApiResponse<List<CourseListItemResponse>> getCourses(@PathVariable UUID restaurantId) {

        List<CourseListItemResult> results = restaurantCourseQueryService.getCourses(restaurantId);

        List<CourseListItemResponse> responses = results.stream()
                .map(result -> CourseListItemResponse.from(result))
                .toList();

        return ApiResponse.ok(responses);
    }
}
