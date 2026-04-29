package com.michelet.restaurant.presentation.dto;

import com.michelet.restaurant.domain.model.CourseSessionType;
import com.michelet.restaurant.domain.model.CourseStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateCourseRequest(

        @NotBlank(message = "코스명은 필수입니다.")
        @Size(max = 100, message = "코스명은 100자를 초과할 수 없습니다.")
        String name,

        @NotNull(message = "코스 가격은 필수입니다.")
        @Min(value = 1, message = "코스 가격은 1 이상이어야 합니다.")
        Long price,

        // 런치/디너 등 코스가 제공되는 세션 타입
        @NotNull(message = "코스 세션 타입은 필수입니다.")
        CourseSessionType sessionType,

        // 코스 판매 가능 상태
        @NotNull(message = "코스 상태는 필수입니다.")
        CourseStatus status,

        // 코스의 구조화된 메뉴 목록
        // 클라이언트는 menuComposition을 직접 보내지 않고, 서버가 menus를 기반으로 생성
        @Valid
        @NotEmpty(message = "코스 메뉴는 1개 이상 필요합니다.")
        List<CreateCourseRequest> menus

) {
}
