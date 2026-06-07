package com.alphateckplus.potify.pool.infrastructure.secondary.notification;

import com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationEventPublisherAdapter implements NotificationEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "potify-notifications";

    @Override
    public void publish(String userId, String type, String title, String content) {
        log.info("Publishing notification event to Kafka: user={}, type={}", userId, type);
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("userId", userId);
            event.put("type", type);
            event.put("title", title);
            event.put("content", content);
            event.put("channel", "NOTIF_APP");

            kafkaTemplate.send(TOPIC, userId, event);
            log.info("Notification event published successfully for user {}", userId);
        } catch (Exception e) {
            log.error("Failed to publish notification event to Kafka", e);
        }
    }
}
