package com.alphateckplus.potify.notification.application_service.primary.notification;

import com.alphateckplus.potify.notification.domain.model.Notification;
import java.util.List;

public interface GetNotificationsService {
    List<Notification> execute(String userId);
}
