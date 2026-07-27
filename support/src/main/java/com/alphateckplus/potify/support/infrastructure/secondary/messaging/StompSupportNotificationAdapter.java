package com.alphateckplus.potify.support.infrastructure.secondary.messaging;

import com.alphateckplus.potify.support.application_service.secondary.SupportNotificationPort;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompSupportNotificationAdapter implements SupportNotificationPort {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void broadcastToConversation(String conversationId, SupportMessageDto message) {
        messagingTemplate.convertAndSend("/topic/support/conversation/" + conversationId, message);
    }

    @Override
    public void notifyAdminQueueUpdate() {
        messagingTemplate.convertAndSend("/topic/support/admin/queue", "REFRESH_QUEUE");
    }
}
