package com.michelet.restaurantservice.global.config;

import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class JpaAuditingConfig {

    private static final UUID SYSTEM_AUDITOR_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> {
            String userId = MDC.get("userId");

            // userId가 없으면 MVP 단계에서는 시스템 UUID 사용
            if (userId == null || userId.isBlank()) {
                return Optional.of(SYSTEM_AUDITOR_ID);
            }

            // userId가 존재하지만 UUID 형식이 아니면 500 대신 fallback 처리
            try {
                return Optional.of(UUID.fromString(userId));
            } catch (IllegalArgumentException e) {
                return Optional.of(SYSTEM_AUDITOR_ID);
            }
        };
    }
}