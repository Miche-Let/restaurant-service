package com.michelet.restaurant.application.result;

import com.michelet.restaurant.domain.model.CoursePart;
import com.michelet.restaurant.domain.model.RestaurantCourseMenu;

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