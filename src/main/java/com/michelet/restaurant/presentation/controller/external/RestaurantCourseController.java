package com.michelet.restaurant.presentation.controller.external;

import com.michelet.common.response.ApiResponse;
import com.michelet.restaurant.application.command.CreateCourseCommand;
import com.michelet.restaurant.application.result.CourseResult;
import com.michelet.restaurant.application.service.command.RestaurantCourseCommandService;
import com.michelet.restaurant.presentation.dto.CourseResponse;
import com.michelet.restaurant.presentation.dto.CreateCourseRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/courses")
public class RestaurantCourseController {

    private final RestaurantCourseCommandService restaurantCourseCommandService;

    public RestaurantCourseController(RestaurantCourseCommandService restaurantCourseCommandService) {
        this.restaurantCourseCommandService = restaurantCourseCommandService;
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
}
