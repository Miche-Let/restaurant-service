package com.michelet.restaurant.application.result;

import com.michelet.restaurant.domain.model.CourseSessionType;
import com.michelet.restaurant.domain.model.CourseStatus;
import com.michelet.restaurant.domain.model.RestaurantCourse;

import java.util.List;
import java.util.UUID;

public record CourseListItemResult(
        UUID courseId,
        String name,
        Long price,

        // menus[]를 기반으로 서버가 생성해 저장한 사용자 노출용 메뉴 구성 요약 문자열
        String menuComposition,

        // 런치/디너 등 코스 제공 세션 타입
        CourseSessionType sessionType,
        CourseStatus status,

        // 코스에 포함된 구조화된 메뉴 목록
        List<CourseMenuResult> menus
) {

    public static CourseListItemResult of(RestaurantCourse course, List<CourseMenuResult> menus) {

        return new CourseListItemResult(
                course.getCourseId(),
                course.getName(),
                course.getPrice(),
                course.getMenuComposition(),
                course.getSessionType(),
                course.getStatus(),
                menus
        );
    }
}
