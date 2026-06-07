package com.alphateckplus.potify.notification.infrastructure.primary.notification;

import com.alphateckplus.potify.notification.application_service.primary.notification.CreateNotificationService;
import com.alphateckplus.potify.notification.domain.model.Notification;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaConsumer {

    private final CreateNotificationService createNotificationService;

    @KafkaListener(topics = "potify-notifications", groupId = "potify-group")
    public void listen(Map<String, Object> payload) {
        log.info("Received notification event: {}", payload);
        try {
            String userId = (String) payload.get("userId");
            String type = (String) payload.get("type");
            String title = (String) payload.get("title");
            String content = (String) payload.get("content");
            String channel = (String) payload.get("channel");

            Notification notification = Notification.builder()
                    .userId(userId)
                    .type(type)
                    .title(title)
                    .content(content)
                    .channel(channel != null ? channel : "NOTIF_APP")
                    .status("ACTIVE")
                    .build();

            createNotificationService.execute(notification);
            log.info("Successfully processed and saved notification for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to process notification event: {}", payload, e);
        }
    }
}
