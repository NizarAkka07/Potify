package com.alphateckplus.potify.support.application_service.secondary;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;

public interface SupportNotificationPort {

    void broadcastToConversation(String conversationId, SupportMessageDto message);

    void notifyAdminQueueUpdate();
}
