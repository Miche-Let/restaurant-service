package com.michelet.restaurant.presentation.dto;

import com.michelet.restaurant.domain.model.CoursePart;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCourseMenuRequest(

        // 코스 메뉴가 어느 파트에 속하는지 나타냄(AMUSE_BOUCHE, APPETIZER, FISH, MAIN, DESSERT)
        @NotNull(message = "코스 파트는 필수입니다.")
        CoursePart coursePart,

        // 사용자에게 노출될 개별 메뉴명
        @NotNull(message = "메뉴명은 필수입니다.")
        @Size(max = 100, message = "메뉴명은 100자를 초과할 수 없습니다.")
        String menuName,

        // 한 코스 안에서 메뉴가 노출되는 순서다.
        // 서버는 이 값을 기준으로 menuComposition을 생성
        @NotNull(message = "정렬 순서는 필수입니다.")
        @Min(value = 1, message = "정렬 순서는 1 이상이어야 합니다.")
        Integer sortOrder


) {
}
