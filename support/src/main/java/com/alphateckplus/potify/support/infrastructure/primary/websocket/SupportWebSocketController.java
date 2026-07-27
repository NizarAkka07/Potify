package com.alphateckplus.potify.support.infrastructure.primary.websocket;

import com.alphateckplus.potify.support.application_service.primary.user.send_user_message.SendUserMessageUseCase;
import com.alphateckplus.potify.support.application_service.secondary.SupportMessageRepositoryPort;
import com.alphateckplus.potify.support.infrastructure.primary.dto.MarkAsReadRequest;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SendMessageRequest;
import com.alphateckplus.potify.support.infrastructure.primary.dto.TypingEventDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SupportWebSocketController {

    private final SendUserMessageUseCase sendUserMessageUseCase;
    private final SupportMessageRepositoryPort messageRepositoryPort;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/support.sendMessage")
    public void handleSendMessage(Principal principal, @Payload SendMessageRequest request) {
        if (principal == null) {
            log.warn("Tentative d'envoi de message WebSocket non authentifiée.");
            return;
        }
        try {
            sendUserMessageUseCase.sendMessageFromUser(principal.getName(), request);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de message STOMP: {}", e.getMessage());
        }
    }

    @MessageMapping("/support.typing")
    public void handleTyping(Principal principal, @Payload TypingEventDto typingEvent) {
        if (principal == null) return;
        messagingTemplate.convertAndSend(
                "/topic/support/conversation/" + typingEvent.getConversationId(),
                typingEvent
        );
    }

    @MessageMapping("/support.markAsRead")
    public void handleMarkAsRead(Principal principal, @Payload MarkAsReadRequest request) {
        if (principal == null) return;
        messageRepositoryPort.markAllMessagesAsRead(request.getConversationId());
        messagingTemplate.convertAndSend(
                "/topic/support/conversation/" + request.getConversationId(),
                "READ_RECEIPT"
        );
    }
}
