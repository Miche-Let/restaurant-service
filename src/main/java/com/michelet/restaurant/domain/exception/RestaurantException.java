package com.michelet.restaurant.domain.exception;

import com.michelet.common.exception.BusinessException;
import com.michelet.common.exception.ErrorCode;

public class RestaurantException extends BusinessException {

    public RestaurantException(RestaurantErrorCode errorCode) {
        super(errorCode);
    }
}
