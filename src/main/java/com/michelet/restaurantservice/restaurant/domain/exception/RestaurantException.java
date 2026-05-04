package com.michelet.restaurantservice.restaurant.domain.exception;

import com.michelet.common.exception.BusinessException;

public class RestaurantException extends BusinessException {

    public RestaurantException(RestaurantErrorCode errorCode) {
        super(errorCode);
    }
}
