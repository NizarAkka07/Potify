package com.alphateckplus.potify.notification.application_service.primary.notification;

import com.alphateckplus.potify.notification.domain.model.Notification;

public interface CreateNotificationService {
    Notification execute(Notification notification);
}
