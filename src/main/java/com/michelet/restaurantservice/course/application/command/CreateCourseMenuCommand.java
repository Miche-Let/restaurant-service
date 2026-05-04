package com.michelet.restaurantservice.course.application.command;

import com.michelet.restaurantservice.course.domain.model.CoursePart;
import com.michelet.restaurantservice.course.presentation.dto.CreateCourseMenuRequest;

public record CreateCourseMenuCommand(

        CoursePart coursePart,
        String menuName,
        Integer sortOrder

) {

    public static CreateCourseMenuCommand from(CreateCourseMenuRequest request) {
        return new CreateCourseMenuCommand(
                request.coursePart(),
                request.menuName(),
                request.sortOrder()
        );
    }
}
