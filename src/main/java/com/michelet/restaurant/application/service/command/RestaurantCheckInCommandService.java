package com.michelet.restaurant.application.service.command;

import com.michelet.common.auth.core.enums.UserRole;
import com.michelet.common.response.ApiResponse;
import com.michelet.restaurant.application.command.CheckInCommand;
import com.michelet.restaurant.application.result.CheckInResult;
import com.michelet.restaurant.domain.exception.CheckInErrorCode;
import com.michelet.restaurant.domain.exception.CheckInException;
import com.michelet.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurant.domain.model.Restaurant;
import com.michelet.restaurant.domain.model.RestaurantCheckInLog;
import com.michelet.restaurant.domain.repository.RestaurantCheckInLogRepository;
import com.michelet.restaurant.domain.repository.RestaurantRepository;
import com.michelet.restaurant.infrastructure.client.ReservationClient;
import com.michelet.restaurant.infrastructure.client.dto.ReservationCheckInRequest;
import com.michelet.restaurant.infrastructure.client.dto.ReservationCheckInResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RestaurantCheckInCommandService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantCheckInLogRepository restaurantCheckInLogRepository;
    private final ReservationClient reservationClient;

    public RestaurantCheckInCommandService(RestaurantRepository restaurantRepository, RestaurantCheckInLogRepository restaurantCheckInLogRepository, ReservationClient reservationClient) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantCheckInLogRepository = restaurantCheckInLogRepository;
        this.reservationClient = reservationClient;
    }

    // 예약 체크인 처리를 수행
    @Transactional
    public CheckInResult checkIn(CheckInCommand command) {
        Restaurant restaurant = getRestaurant(command.restaurantId());
        validateCheckInAuthority(restaurant, command);

        ReservationCheckInResponse reservationCheckInResponse = requestReservationCheckIn(command);
        validateReservationCheckInResponse(reservationCheckInResponse);

        RestaurantCheckInLog checkInLog = RestaurantCheckInLog.createCheckedIn(
                command.restaurantId(),
                command.reservationId(),
                reservationCheckInResponse.visitDate(),
                command.checkedInBy(),
                reservationCheckInResponse.checkedInAt()
        );

        RestaurantCheckInLog savedCheckInLog = restaurantCheckInLogRepository.save(checkInLog);

        return CheckInResult.from(savedCheckInLog);
    }

    private Restaurant getRestaurant(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_404_NOT_FOUND));
    }

    // 체크인 처리 권한을 검증
    private void validateCheckInAuthority(Restaurant restaurant, CheckInCommand command) {
        if (command.userRole() == UserRole.MASTER) {
            return;
        }

        if (command.userRole() == UserRole.OWNER
                && restaurant.getOwnerId().equals(command.checkedInBy())) {
            return;
        }

        throw new CheckInException(CheckInErrorCode.CHECKIN_403_FORBIDDEN);
    }

    private ReservationCheckInResponse requestReservationCheckIn(CheckInCommand command) {
        ApiResponse<ReservationCheckInResponse> response = reservationClient.checkInReservation(
                ReservationCheckInRequest.of(
                        command.reservationId(),
                        command.restaurantId()
                ),
                command.checkedInBy(),
                command.userRole().name()
        );

        if (response == null || response.data() == null) {
            throw new CheckInException(CheckInErrorCode.CHECKIN_502_INVALID_RESERVATION_RESPONSE);
        }

        return response.data();
    }

    // reservation-service 체크인 응답을 검증
    private void validateReservationCheckInResponse(ReservationCheckInResponse response) {
        if (response.visitDate() == null || response.checkedInAt() == null) {
            throw new CheckInException(CheckInErrorCode.CHECKIN_502_INVALID_RESERVATION_RESPONSE);
        }

        if (!response.isCheckedIn()) {
            throw new CheckInException(CheckInErrorCode.CHECKIN_502_INVALID_RESERVATION_RESPONSE);
        }
    }
}