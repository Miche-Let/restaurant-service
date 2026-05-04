package com.michelet.restaurantservice.restaurant.domain.model.vo;

import java.time.LocalTime;

public record ReservationOpenAt(LocalTime value) {

    public ReservationOpenAt {
        validate(value);
    }

    public static ReservationOpenAt of(LocalTime value) {
        return new ReservationOpenAt(value);
    }

    private static void validate(LocalTime value) {
        if (value == null) {
            throw new IllegalArgumentException("예약 오픈 시각은 필수입니다.");
        }
    }
}