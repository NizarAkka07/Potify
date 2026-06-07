package com.alphateckplus.potify.payment.application_service.secondary.notification;

public interface NotificationEventPublisherPort {
    void publish(String userId, String type, String title, String content);
}
