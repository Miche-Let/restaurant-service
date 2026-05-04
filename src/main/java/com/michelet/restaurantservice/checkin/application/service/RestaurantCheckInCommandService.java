package com.michelet.restaurantservice.checkin.application.service;

import com.michelet.common.auth.core.enums.UserRole;
import com.michelet.common.response.ApiResponse;
import com.michelet.restaurantservice.checkin.application.command.CheckInCommand;
import com.michelet.restaurantservice.checkin.application.result.CheckInResult;
import com.michelet.restaurantservice.checkin.domain.exception.CheckInErrorCode;
import com.michelet.restaurantservice.checkin.domain.exception.CheckInException;
import com.michelet.restaurantservice.domain.exception.RestaurantErrorCode;
import com.michelet.restaurantservice.domain.exception.RestaurantException;
import com.michelet.restaurantservice.domain.model.Restaurant;
import com.michelet.restaurantservice.checkin.domain.model.RestaurantCheckInLog;
import com.michelet.restaurantservice.checkin.domain.repository.RestaurantCheckInLogRepository;
import com.michelet.restaurantservice.domain.repository.RestaurantRepository;
import com.michelet.restaurantservice.checkin.infrastructure.client.ReservationClient;
import com.michelet.restaurantservice.checkin.infrastructure.client.dto.ReservationCheckInRequest;
import com.michelet.restaurantservice.checkin.infrastructure.client.dto.ReservationCheckInResponse;
import feign.FeignException;
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
        try {
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
        } catch (FeignException.NotFound exception) {
            throw new CheckInException(CheckInErrorCode.CHECKIN_404_RESERVATION_NOT_FOUND);
        } catch (FeignException.BadRequest exception) {
            throw new CheckInException(CheckInErrorCode.CHECKIN_400_RESERVATION_CHECK_IN_FAILED);
        } catch (FeignException.Conflict exception) {
            throw new CheckInException(CheckInErrorCode.CHECKIN_409_RESERVATION_CHECK_IN_FAILED);
        } catch (FeignException exception) {
            throw new CheckInException(CheckInErrorCode.CHECKIN_502_INVALID_RESERVATION_RESPONSE);
        }
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