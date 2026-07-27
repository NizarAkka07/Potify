package com.alphateckplus.potify.support.application_service.primary.admin.send_admin_message;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.data_jpa.entity.support.MessageSenderType;
import com.alphateckplus.potify.data_jpa.entity.support.MessageStatus;
import com.alphateckplus.potify.support.application_service.secondary.SupportConversationRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportMessageRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportNotificationPort;
import com.alphateckplus.potify.support.domain.exception.ConversationNotFoundException;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import com.alphateckplus.potify.support.domain.model.SupportMessage;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SendMessageRequest;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultSendAdminMessageService implements SendAdminMessageUseCase {

    private final SupportConversationRepositoryPort conversationRepositoryPort;
    private final SupportMessageRepositoryPort messageRepositoryPort;
    private final SupportNotificationPort notificationPort;

    @Override
    @Transactional
    public SupportMessageDto sendMessageFromAdmin(String adminEmail, SendMessageRequest request) {
        SupportConversation conversation = conversationRepositoryPort.findById(request.getConversationId())
                .orElseThrow(() -> new ConversationNotFoundException("Conversation non trouvée"));

        if (!ConversationStatus.AGENT_ASSIGNED.equals(conversation.getStatus())) {
            throw new IllegalStateException("La conversation doit être assignée avant de pouvoir répondre");
        }

        SupportMessage adminMsg = SupportMessage.builder()
                .conversationId(conversation.getId())
                .senderType(MessageSenderType.ADMIN)
                .senderId(adminEmail)
                .senderName(conversation.getAssignedAdminName() != null ? conversation.getAssignedAdminName() : "Support Client")
                .content(request.getContent())
                .status(MessageStatus.SENT)
                .createdAt(Instant.now())
                .build();
        adminMsg = messageRepositoryPort.save(adminMsg);

        conversation.setLastMessageAt(Instant.now());
        conversationRepositoryPort.save(conversation);

        SupportMessageDto dto = mapMessageToDto(adminMsg);
        notificationPort.broadcastToConversation(conversation.getId(), dto);

        return dto;
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
