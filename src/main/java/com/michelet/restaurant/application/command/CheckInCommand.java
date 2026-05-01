package com.michelet.restaurant.application.command;

import java.util.UUID;

public record CheckInCommand(
        UUID restaurantId,
        UUID reservationId,
        UUID checkedInBy
) {

    public static CheckInCommand of(
            UUID restaurantId,
            UUID reservationId,
            UUID checkedInBy
    ) {
        return new CheckInCommand(
                validateRestaurantId(restaurantId),
                validateReservationId(reservationId),
                validateCheckedInBy(checkedInBy)
        );
    }

    private static UUID validateRestaurantId(UUID restaurantId) {
        if (restaurantId == null) {
            throw new IllegalArgumentException("식당 ID는 필수입니다.");
        }
        return restaurantId;
    }

    private static UUID validateReservationId(UUID reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }
        return reservationId;
    }

    private static UUID validateCheckedInBy(UUID checkedInBy) {
        if (checkedInBy == null) {
            throw new IllegalArgumentException("체크인 처리자 ID는 필수입니다.");
        }
        return checkedInBy;
    }

}
