package com.michelet.restaurantservice.checkin.infrastructure.kafka.event.subscribe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CheckInCompletedEvent(
        UUID eventId,
        String eventType,
        UUID reservationId,
        UUID restaurantId,
        LocalDate visitDate,
        UUID checkedInBy,
        LocalDateTime checkedInAt,
        LocalDateTime eventCreatedAt
) {

    public static final String CHECK_IN_COMPLETED = "CHECK_IN_COMPLETED";

    public boolean isCheckInCompleted() {
        return CHECK_IN_COMPLETED.equals(eventType);
    }
}
