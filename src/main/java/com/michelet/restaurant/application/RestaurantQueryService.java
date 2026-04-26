package com.michelet.restaurant.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RestaurantQueryService {

    public String getHealthStatus() {
        return "Restaurant Query Service is Healthy";
    }
}