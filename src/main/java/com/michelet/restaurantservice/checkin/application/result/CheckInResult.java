package com.michelet.restaurantservice.checkin.application.result;

import com.michelet.restaurantservice.checkin.domain.model.CheckInStatus;
import com.michelet.restaurantservice.checkin.domain.model.RestaurantCheckInLog;

import java.time.LocalDateTime;
import java.util.UUID;

public record CheckInResult(
        UUID restaurantId,
        UUID reservationId,
        CheckInStatus status,
        LocalDateTime checkedInAt
) {
    public static CheckInResult from(RestaurantCheckInLog checkInLog) {
        return new CheckInResult(
                checkInLog.getRestaurantId(),
                checkInLog.getReservationId(),
                checkInLog.getStatus(),
                checkInLog.getCheckedInAt()
        );
    }
}
