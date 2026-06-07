package com.alphateckplus.potify.notification.application_service.primary.notification;

import com.alphateckplus.potify.notification.application_service.secondary.notification.NotificationRepositoryPort;
import com.alphateckplus.potify.notification.domain.model.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultGetNotificationsService implements GetNotificationsService {

    private final NotificationRepositoryPort repositoryPort;

    @Override
    public List<Notification> execute(String userId) {
        return repositoryPort.findByUserId(userId);
    }
}
