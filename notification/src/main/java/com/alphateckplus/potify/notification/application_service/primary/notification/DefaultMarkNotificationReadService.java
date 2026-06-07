package com.alphateckplus.potify.notification.application_service.primary.notification;

import com.alphateckplus.potify.notification.application_service.secondary.notification.NotificationRepositoryPort;
import com.alphateckplus.potify.notification.domain.model.Notification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultMarkNotificationReadService implements MarkNotificationReadService {

    private final NotificationRepositoryPort repositoryPort;

    @Override
    public Notification execute(String id) {
        Notification notification = repositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable avec l'ID: " + id));
        notification.setStatus("COMPLETED"); // COMPLETED represents the read status
        return repositoryPort.save(notification);
    }
}
