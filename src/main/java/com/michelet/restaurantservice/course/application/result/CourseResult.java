package com.michelet.restaurantservice.course.application.result;

import com.michelet.restaurantservice.course.domain.model.RestaurantCourse;

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
