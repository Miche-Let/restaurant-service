package com.michelet.restaurant.application.command;

import com.michelet.restaurant.domain.model.CoursePart;
import com.michelet.restaurant.presentation.dto.CreateCourseMenuRequest;

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
