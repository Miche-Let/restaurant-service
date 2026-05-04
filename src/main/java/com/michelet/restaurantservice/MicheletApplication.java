package com.michelet.restaurantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MicheletApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicheletApplication.class, args);
    }

}
