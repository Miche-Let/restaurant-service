package com.michelet.restaurant.infrastructure.client;

import com.michelet.common.response.ApiResponse;
import com.michelet.restaurant.infrastructure.client.dto.ReservationCheckInRequest;
import com.michelet.restaurant.infrastructure.client.dto.ReservationCheckInResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "reservation-service")
public interface ReservationClient {

    @PatchMapping("/internal/reservations/check-in")
    ApiResponse<ReservationCheckInResponse> checkInReservation(@RequestBody ReservationCheckInRequest request,
                                                               @RequestHeader("X-User-Id") UUID userId,
                                                               @RequestHeader("X-User-Role") String userRole);
}
