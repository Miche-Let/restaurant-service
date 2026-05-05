package com.michelet.restaurantservice.course.presentation.dto;

import java.util.UUID;

public record CourseResponse(

        UUID courseId,
        UUID restaurantId
) {

    public static CourseResponse of(UUID courseId, UUID restaurantId) {
        return new CourseResponse(courseId, restaurantId);
    }
}
