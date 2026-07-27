package com.alphateckplus.potify.support.application_service.primary.admin.resolve_admin_conversation;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.data_jpa.entity.support.MessageSenderType;
import com.alphateckplus.potify.data_jpa.entity.support.MessageStatus;
import com.alphateckplus.potify.support.application_service.secondary.SupportConversationRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportMessageRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportNotificationPort;
import com.alphateckplus.potify.support.domain.exception.ConversationNotFoundException;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import com.alphateckplus.potify.support.domain.model.SupportMessage;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultResolveAdminConversationService implements ResolveAdminConversationUseCase {

    private final SupportConversationRepositoryPort conversationRepositoryPort;
    private final SupportMessageRepositoryPort messageRepositoryPort;
    private final SupportNotificationPort notificationPort;

    @Override
    @Transactional
    public SupportConversationDto resolveConversation(String adminEmail, String conversationId) {
        SupportConversation conversation = conversationRepositoryPort.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation non trouvée"));

        conversation.setStatus(ConversationStatus.RESOLVED);
        if (adminEmail != null && conversation.getAssignedAdminId() == null) {
            conversation.setAssignedAdminId(adminEmail);
            conversation.setAssignedAdminName(adminEmail);
        }
        conversation = conversationRepositoryPort.save(conversation);

        SupportMessage sysMsg = SupportMessage.builder()
                .conversationId(conversation.getId())
                .senderType(MessageSenderType.SYSTEM)
                .content("✅ La conversation a été résolue par le support.")
                .status(MessageStatus.SENT)
                .createdAt(Instant.now())
                .build();
        sysMsg = messageRepositoryPort.save(sysMsg);

        notificationPort.broadcastToConversation(conversation.getId(), mapMessageToDto(sysMsg));
        notificationPort.notifyAdminQueueUpdate();

        return mapConversationToDto(conversation);
    }

    private SupportConversationDto mapConversationToDto(SupportConversation model) {
        return SupportConversationDto.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .userName(model.getUserName())
                .userEmail(model.getUserEmail())
                .userAvatar(model.getUserAvatar())
                .assignedAdminId(model.getAssignedAdminId())
                .assignedAdminName(model.getAssignedAdminName())
                .status(model.getStatus())
                .subject(model.getSubject())
                .lastMessageAt(model.getLastMessageAt())
                .createdAt(model.getCreatedAt())
                .build();
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
