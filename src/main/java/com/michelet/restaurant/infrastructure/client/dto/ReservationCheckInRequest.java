package com.michelet.restaurant.infrastructure.client.dto;

import java.util.UUID;

// reservation-service 내부 체크인 API 요청 DTO
public record ReservationCheckInRequest(
        UUID reservationId,
        UUID restaurantId
) {
    public static ReservationCheckInRequest of(
            UUID reservationId,
            UUID restaurantId
    ) {
        return new ReservationCheckInRequest(
                validateReservationId(reservationId),
                validateRestaurantId(restaurantId)
        );
    }

    private static UUID validateReservationId(UUID reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }
        return reservationId;
    }

    private static UUID validateRestaurantId(UUID restaurantId) {
        if (restaurantId == null) {
            throw new IllegalArgumentException("식당 ID는 필수입니다.");
        }
        return restaurantId;
    }

}
