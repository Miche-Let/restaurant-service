package com.michelet.restaurant.presentation.dto;

import com.michelet.restaurant.application.result.CourseMenuResult;
import com.michelet.restaurant.domain.model.CoursePart;

public record CourseMenuResponse(
        // 코스 구성 구간(AMUSE_BOUCHE, APPETIZER, FISH, MAIN, DESSERT)
        CoursePart coursePart,
        // 메뉴명
        String menuName,
        // 코스 내 노출 순서
        Integer sortOrder
) {

    public static CourseMenuResponse from(CourseMenuResult result) {
        return new CourseMenuResponse(
                result.coursePart(),
                result.menuName(),
                result.sortOrder()
        );
    }
}
