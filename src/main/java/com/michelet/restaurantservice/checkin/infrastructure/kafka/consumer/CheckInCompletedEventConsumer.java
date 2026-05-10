package com.michelet.restaurantservice.checkin.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michelet.restaurantservice.checkin.application.service.CheckInCompletedEventHandler;
import com.michelet.restaurantservice.checkin.infrastructure.kafka.event.subscribe.CheckInCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CheckInCompletedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(CheckInCompletedEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final CheckInCompletedEventHandler checkInCompletedEventHandler;

    public CheckInCompletedEventConsumer(ObjectMapper objectMapper, CheckInCompletedEventHandler checkInCompletedEventHandler) {
        this.objectMapper = objectMapper;
        this.checkInCompletedEventHandler = checkInCompletedEventHandler;
    }

    // reservation-service에서 발행한 체크인 완료 이벤트를 수신
    @KafkaListener(
            topics = "${kafka.topic.reservation-check-in-completed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) {
        CheckInCompletedEvent event = parseEvent(message);

        log.info(
                "체크인 완료 이벤트를 수신 eventId={},reservationId={},restaurantId={}",
                event.eventId(),
                event.reservationId(),
                event.restaurantId()
        );

        checkInCompletedEventHandler.handle(event);
    }

    // Kafka에서 수신한 JSON 문자열을 CheckInCompletedEvent로 변환
    private CheckInCompletedEvent parseEvent(String message) {
        try {
            return objectMapper.readValue(message, CheckInCompletedEvent.class);
        } catch (JsonProcessingException exception) {
            log.error("체크인 완료 이벤트 역직렬화에 실패 message={}", message, exception);
            throw new IllegalArgumentException("체크인 완료 이벤트가 올바르지 않습니다", exception);
        }
    }
}
