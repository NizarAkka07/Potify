package com.alphateckplus.potify.pool.application_service.secondary.notification;

public interface NotificationPort {
    void sendInvitationEmail(String email, String poolName, String token);
}
