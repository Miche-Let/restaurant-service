package com.michelet.restaurantservice.checkin.presentation.controller.external;

import com.michelet.common.auth.core.annotation.RequireRole;
import com.michelet.common.auth.core.context.UserContext;
import com.michelet.common.auth.core.enums.UserRole;
import com.michelet.common.auth.webmvc.context.UserContextHolder;
import com.michelet.common.response.ApiResponse;
import com.michelet.restaurantservice.checkin.application.command.CheckInCommand;
import com.michelet.restaurantservice.checkin.application.result.CheckInResult;
import com.michelet.restaurantservice.checkin.application.service.RestaurantCheckInCommandService;
import com.michelet.restaurantservice.domain.exception.RestaurantErrorCode;
import com.michelet.restaurantservice.domain.exception.RestaurantException;
import com.michelet.restaurantservice.checkin.presentation.dto.CheckInResponse;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantCheckInController {

    private final RestaurantCheckInCommandService restaurantCheckInCommandService;

    public RestaurantCheckInController(RestaurantCheckInCommandService restaurantCheckInCommandService) {
        this.restaurantCheckInCommandService = restaurantCheckInCommandService;
    }

    @RequireRole({UserRole.OWNER, UserRole.MASTER})
    @PatchMapping("/{restaurantId}/reservations/{reservationId}/check-in")
    public ApiResponse<CheckInResponse> checkIn(@PathVariable UUID restaurantId, @PathVariable UUID reservationId) {

        UserContext userContext = getAuthenticatedUserContext();

        CheckInCommand command = CheckInCommand.of(
                restaurantId,
                reservationId,
                parseAuthenticatedUserId(userContext.userId()),
                userContext.role()
        );

        CheckInResult result = restaurantCheckInCommandService.checkIn(command);

        return ApiResponse.ok(CheckInResponse.from(result));
    }

    private UserContext getAuthenticatedUserContext() {
        UserContext userContext = UserContextHolder.get();

        if (userContext == null) {
            throw new RestaurantException(RestaurantErrorCode.RESTAURANT_401_INVALID_AUTH_USER_ID);
        }

        return userContext;
    }

    private UUID parseAuthenticatedUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new RestaurantException(RestaurantErrorCode.RESTAURANT_401_INVALID_AUTH_USER_ID);
        }

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException exception) {
            throw new RestaurantException(RestaurantErrorCode.RESTAURANT_401_INVALID_AUTH_USER_ID);
        }
    }
}
