package com.michelet.restaurantservice.course.presentation.dto;

import com.michelet.restaurantservice.course.application.result.CourseListItemResult;
import com.michelet.restaurantservice.course.domain.model.CourseSessionType;
import com.michelet.restaurantservice.course.domain.model.CourseStatus;

import java.util.List;
import java.util.UUID;

public record CourseListItemResponse(
        UUID courseId,
        String name,
        Long price,
        String menuComposition,
        CourseSessionType sessionType,
        CourseStatus status,
        // 코스에 포함된 구조화된 메뉴 목록
        List<CourseMenuResponse> menus
) {

    public static CourseListItemResponse from(CourseListItemResult result) {
        return new CourseListItemResponse(
                result.courseId(),
                result.name(),
                result.price(),
                result.menuComposition(),
                result.sessionType(),
                result.status(),
                result.menus().stream()
                        .map(menu -> CourseMenuResponse.from(menu))
                        .toList()
        );
    }
}
