package com.michelet.restaurantservice.course.presentation.dto;

import com.michelet.restaurantservice.course.application.result.CourseSummaryResult;
import com.michelet.restaurantservice.course.domain.model.CourseSessionType;
import com.michelet.restaurantservice.course.domain.model.CourseStatus;

import java.util.UUID;

// 식당 상세 응답에 들어갈 코스 요약 Response DTO
public record CourseSummaryResponse(
        UUID courseId,
        String name,
        Long price,

        // 사용자에게 노출할 코스 메뉴 요약 문자열
        // 개별 menus[]가 아니라 서버가 생성한 요약값만 내림
        String menuComposition,

        // 런치/디너 등 코스 제공 세션 타입
        CourseSessionType sessionType,
        CourseStatus status
) {

    public static CourseSummaryResponse from(CourseSummaryResult result) {
        return new CourseSummaryResponse(
                result.courseId(),
                result.name(),
                result.price(),
                result.menuComposition(),
                result.sessionType(),
                result.status()
        );
    }
}
