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
        publish(userId, null, type, title, content);
    }

    @Override
    public void publish(String userId, String email, String type, String title, String content) {
        log.info("Publishing notification event to Kafka asynchronously: user={}, email={}, type={}", userId, email, type);
        Map<String, Object> event = new HashMap<>();
        event.put("userId", userId);
        event.put("email", email);
        event.put("type", type);
        event.put("title", title);
        event.put("content", content);
        event.put("channel", "NOTIF_APP");

        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                String key = userId != null ? userId : email;
                kafkaTemplate.send(TOPIC, key, event);
                log.info("Notification event published successfully to Kafka for user {} / email {}", userId, email);
            } catch (Exception e) {
                log.error("Failed to publish notification event to Kafka asynchronously", e);
            }
        });
    }
}
