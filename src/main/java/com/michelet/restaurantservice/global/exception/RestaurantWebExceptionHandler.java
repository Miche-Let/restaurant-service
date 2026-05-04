package com.michelet.restaurantservice.global.exception;

import com.michelet.common.response.ApiResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class RestaurantWebExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(
            ResponseStatusException exception
    ) {
        HttpStatusCode statusCode = exception.getStatusCode();

        return ResponseEntity
                .status(statusCode)
                .body(ApiResponse.fail(
                        resolveErrorCode(statusCode),
                        exception.getReason()
                ));
    }

    private String resolveErrorCode(HttpStatusCode statusCode) {
        int status = statusCode.value();

        if (status == 401 || status == 403) {
            return "AUTH_" + status;
        }

        return "WEB_" + status;
    }
}