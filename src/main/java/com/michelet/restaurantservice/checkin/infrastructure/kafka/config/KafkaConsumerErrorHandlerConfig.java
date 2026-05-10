package com.michelet.restaurantservice.checkin.infrastructure.kafka.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerErrorHandlerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerErrorHandlerConfig.class);

    /**
     * Kafka Consumer 처리 실패 정책을 설정
     *
     * IllegalArgumentException은 잘못된 지원하지 않는 이벤트 타입처럼
     * 재시도해도 성공 가능성이 낮은 오류이므로 재시도X
     *
     * 그 외 예외는 일시적인 DB 오류나 외부 의존성 문제일 수 있으므로 제한적으로 재시도
     */
    @Bean
    public CommonErrorHandler kafkaConsumerErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, exception) -> log.error(
                        "Kafka 체크인 이벤트 처리에 실패하여 메시지를 스킵합니다 topic={}, partition={}, offset={}, key={}",
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.key(),
                        exception
                ),
                new FixedBackOff(1_000L, 3L)
        );

        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

        return errorHandler;
    }
}
