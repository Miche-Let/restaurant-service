package com.michelet.restaurant.application.result;

import com.michelet.restaurant.domain.model.CourseSessionType;
import com.michelet.restaurant.domain.model.CourseStatus;
import com.michelet.restaurant.domain.model.RestaurantCourse;

import java.util.UUID;

public record CourseSummaryResult(

        UUID courseId,
        String name,
        Long price,

        // 사용자에게 노출할 코스 메뉴 요약 문자열
        // menus[]를 기반으로 서버에서 생성해 저장한 값
        String menuComposition,

        // 런치/디너 등 코스 제공 세션 타입
        CourseSessionType sessionType,

        // 코스 판매 가능 상태
        CourseStatus status
) {

    public static CourseSummaryResult from(RestaurantCourse course) {
        return new CourseSummaryResult(
                course.getCourseId(),
                course.getName(),
                course.getPrice(),
                course.getMenuComposition(),
                course.getSessionType(),
                course.getStatus()
        );
    }
}
