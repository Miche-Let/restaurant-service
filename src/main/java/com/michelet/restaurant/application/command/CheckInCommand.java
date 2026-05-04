package com.michelet.restaurant.application.command;

import com.michelet.common.auth.core.enums.UserRole;

import java.util.UUID;

public record CheckInCommand(
        UUID restaurantId,
        UUID reservationId,
        UUID checkedInBy,
        UserRole userRole
) {
    // record canonical constructor 검증
    public CheckInCommand {
        restaurantId = validateRestaurantId(restaurantId);
        reservationId = validateReservationId(reservationId);
        checkedInBy = validateCheckedInBy(checkedInBy);
        userRole = validateUserRole(userRole);
    }

    public static CheckInCommand of(
            UUID restaurantId,
            UUID reservationId,
            UUID checkedInBy,
            UserRole userRole
    ) {
        return new CheckInCommand(
                restaurantId,
                reservationId,
                checkedInBy,
                userRole
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

    private static UserRole validateUserRole(UserRole userRole) {
        if (userRole == null) {
            throw new IllegalArgumentException("체크인 처리자 권한은 필수입니다.");
        }
        return userRole;
    }
}
