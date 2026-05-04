package com.michelet.restaurantservice.course.application.command;

import com.michelet.restaurantservice.course.domain.model.CourseSessionType;
import com.michelet.restaurantservice.course.domain.model.CourseStatus;
import com.michelet.restaurantservice.presentation.dto.CreateCourseRequest;

import java.util.List;
import java.util.UUID;

public record CreateCourseCommand(

        UUID restaurantId,
        String name,
        Long price,
        CourseSessionType sessionType,
        CourseStatus status,

        // 코스에 포함될 구조화된 메뉴 목록
        // 서비스 계층에서 이 목록을 기반으로 menuComposition을 생성
        List<CreateCourseMenuCommand> menus

) {

    public static CreateCourseCommand of(UUID restaurantId, CreateCourseRequest request) {
        return new CreateCourseCommand(
                restaurantId,
                request.name(),
                request.price(),
                request.sessionType(),
                request.status(),
                request.menus().stream()
                        .map(menu -> CreateCourseMenuCommand.from(menu))
                        .toList()
        );
    }
}
