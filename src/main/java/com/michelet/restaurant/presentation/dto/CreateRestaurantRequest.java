package com.michelet.restaurant.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.michelet.restaurant.domain.model.RestaurantStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record CreateRestaurantRequest(

        @NotBlank(message = "식당 이름은 필수입니다.")
        @Size(max = 100, message = "식당 이름은 100자를 초과할 수 없습니다.")
        String name,

        @NotBlank(message = "식당 주소는 필수입니다.")
        @Size(max = 255, message = "식당 주소는 255자를 초과할 수 없습니다.")
        String address,

        @NotBlank(message = "식당 전화번호는 필수입니다.")
        @Size(max = 30, message = "식당 전화번호는 30자를 초과할 수 없습니다.")
        String phone,

        @Size(max = 1000, message = "식당 설명은 1000자를 초과할 수 없습니다.")
        String description,

        @NotNull(message = "예약 오픈 시각은 필수입니다.")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime reservationOpenAt,

        @NotNull(message = "평균 식사 시간은 필수입니다.")
        @Min(value = 1, message = "평균 식사 시간은 1분 이상이어야 합니다.")
        Integer avgMealDurationMin,

        @NotNull(message = "식당 상태는 필수입니다.")
        RestaurantStatus status,

        @NotBlank(message = "운영시간 정보는 필수입니다.")
        @Size(max = 1000, message = "운영시간 정보는 1000자를 초과할 수 없습니다.")
        String businessHours

        ) {
}
