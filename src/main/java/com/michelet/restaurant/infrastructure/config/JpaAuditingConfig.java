package com.michelet.restaurant.infrastructure.config;

import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
public class JpaAuditingConfig {

    /**
     * JPA Auditing에서 createdBy / updatedBy 값을 채울 때 사용하는 Bean
     *
     * 현재 MVP 단계에서는 인증/시큐리티가 아직 완전히 연결되지 않았기 때문에
     * MDC 에 userId 가 있으면 그 값을 사용하고,
     * 없으면 임시 시스템 UUID를 반환하도록 처리
     *
     * 추후 실제 인증이 붙으면 사용자 UUID를 꺼내도록 수정
     */
    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> {
            String userId = MDC.get("userId");

            // 현재 요청 컨텍스트에 사용자 ID가 없는 경우
            // MVP 단계에서는 임시 시스템 UUID를 사용
            // 추후 인증 연동 후 반드시 실제 사용자 UUID 기반으로 변경 필요
            if (userId == null || userId.isBlank()) {
                return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
            }

            return Optional.of(UUID.fromString(userId));
        };
    }
}