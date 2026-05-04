package com.michelet.restaurant.infrastructure.client.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// reservation-service 내부 체크인 API 응답 DTO
public record ReservationCheckInResponse(
        UUID reservationId,
        UUID restaurantId,
        LocalDate visitDate,
        String status,
        LocalDateTime checkedInAt
) {

    private static final String COMPLETED_STATUS  = "COMPLETED";

    // reservation-service 응답 상태가 체크인 완료 상태인지 확인
    public boolean isCheckedIn() {
        return COMPLETED_STATUS.equals(status);
    }
}
