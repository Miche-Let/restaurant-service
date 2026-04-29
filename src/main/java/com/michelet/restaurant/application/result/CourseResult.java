package com.michelet.restaurant.application.result;

import com.michelet.restaurant.domain.model.RestaurantCourse;

import java.util.UUID;

public record CourseResult(

        UUID courseId,
        UUID restaurantId

) {

    public static CourseResult from(RestaurantCourse course) {
        return new CourseResult(
                course.getCourseId(),
                course.getRestaurantId()
        );
    }
}
