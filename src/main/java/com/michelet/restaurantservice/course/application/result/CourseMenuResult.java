package com.michelet.restaurantservice.course.application.result;

import com.michelet.restaurantservice.course.domain.model.CoursePart;
import com.michelet.restaurantservice.course.domain.model.RestaurantCourseMenu;

public record CourseMenuResult(
        CoursePart coursePart,
        String menuName,
        Integer sortOrder
) {

    public static CourseMenuResult from(RestaurantCourseMenu menu) {
        return new CourseMenuResult(
                menu.getCoursePart(),
                menu.getMenuName(),
                menu.getSortOrder()
        );
    }
}