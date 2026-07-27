package com.alphateckplus.potify.support.application_service.primary.user.escalate_conversation;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.data_jpa.entity.support.MessageSenderType;
import com.alphateckplus.potify.data_jpa.entity.support.MessageStatus;
import com.alphateckplus.potify.support.application_service.secondary.SupportConversationRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportMessageRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportNotificationPort;
import com.alphateckplus.potify.support.domain.exception.ConversationNotFoundException;
import com.alphateckplus.potify.support.domain.exception.UnauthorizedSupportAccessException;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import com.alphateckplus.potify.support.domain.model.SupportMessage;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultEscalateConversationService implements EscalateConversationUseCase {

    private final SupportConversationRepositoryPort conversationRepositoryPort;
    private final SupportMessageRepositoryPort messageRepositoryPort;
    private final SupportNotificationPort notificationPort;

    @Override
    @Transactional
    public void escalateConversation(String userEmail, String conversationId) {
        SupportConversation conversation = conversationRepositoryPort.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation non trouvée"));

        if (!conversation.getUserEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedSupportAccessException("Accès non autorisé");
        }

        conversation.setStatus(ConversationStatus.PENDING_AGENT);
        conversationRepositoryPort.save(conversation);

        SupportMessage sysMsg = SupportMessage.builder()
                .conversationId(conversation.getId())
                .senderType(MessageSenderType.SYSTEM)
                .content("🔔 Demande d'assistance transmise à la file d'attente des administrateurs. (Demande explicite de l'utilisateur)")
                .status(MessageStatus.SENT)
                .createdAt(Instant.now())
                .build();
        sysMsg = messageRepositoryPort.save(sysMsg);

        notificationPort.broadcastToConversation(conversation.getId(), mapMessageToDto(sysMsg));
        notificationPort.notifyAdminQueueUpdate();
    }

    private SupportMessageDto mapMessageToDto(SupportMessage model) {
        return SupportMessageDto.builder()
                .id(model.getId())
                .conversationId(model.getConversationId())
                .senderType(model.getSenderType())
                .senderId(model.getSenderId())
                .senderName(model.getSenderName())
                .senderAvatar(model.getSenderAvatar())
                .content(model.getContent())
                .attachmentUrl(model.getAttachmentUrl())
                .attachmentType(model.getAttachmentType())
                .status(model.getStatus())
                .createdAt(model.getCreatedAt())
                .build();
    }
}
