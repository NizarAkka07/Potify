package com.alphateckplus.potify.notification.application_service.secondary.notification;

import com.alphateckplus.potify.notification.domain.model.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    Optional<Notification> findById(String id);
    List<Notification> findByUserId(String userId);
}
