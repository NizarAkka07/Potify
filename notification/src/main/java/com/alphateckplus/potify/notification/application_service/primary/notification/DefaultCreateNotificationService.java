package com.alphateckplus.potify.notification.application_service.primary.notification;

import com.alphateckplus.potify.notification.application_service.secondary.notification.NotificationRepositoryPort;
import com.alphateckplus.potify.notification.domain.model.Notification;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultCreateNotificationService implements CreateNotificationService {

    private final NotificationRepositoryPort repositoryPort;

    @Override
    public Notification execute(Notification notification) {
        if (notification.getStatus() == null) {
            notification.setStatus("ACTIVE"); // ACTIVE represents unread/new notifications
        }
        if (notification.getChannel() == null) {
            notification.setChannel("NOTIF_APP");
        }
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(Instant.now());
        }
        return repositoryPort.save(notification);
    }
}
