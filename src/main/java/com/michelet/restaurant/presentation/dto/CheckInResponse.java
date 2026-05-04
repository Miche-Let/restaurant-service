package com.michelet.restaurant.presentation.dto;

import com.michelet.restaurant.application.result.CheckInResult;
import com.michelet.restaurant.domain.model.CheckInStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CheckInResponse(
        UUID restaurantId,
        UUID reservationId,
        CheckInStatus checkInStatus,
        LocalDateTime checkedInAt
) {
    public static CheckInResponse from(CheckInResult result) {
        return new CheckInResponse(
                result.restaurantId(),
                result.reservationId(),
                result.status(),
                result.checkedInAt()
        );
    }

}
