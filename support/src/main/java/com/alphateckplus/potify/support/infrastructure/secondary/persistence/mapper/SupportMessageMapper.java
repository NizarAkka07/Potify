package com.alphateckplus.potify.support.infrastructure.secondary.persistence.mapper;

import com.alphateckplus.potify.data_jpa.entity.support.MessageSenderType;
import com.alphateckplus.potify.data_jpa.entity.support.SupportMessageEntity;
import com.alphateckplus.potify.support.domain.model.SupportMessage;
import org.springframework.stereotype.Component;

@Component
public class SupportMessageMapper {

    public SupportMessage toDomain(SupportMessageEntity entity) {
        if (entity == null) return null;
        String senderName = entity.getSender() != null ? entity.getSender().getFullName() :
                (MessageSenderType.BOT.equals(entity.getSenderType()) ? "Chatbot Potify" : "Système");

        return SupportMessage.builder()
                .id(entity.getId())
                .conversationId(entity.getConversation() != null ? entity.getConversation().getId() : null)
                .senderType(entity.getSenderType())
                .senderId(entity.getSender() != null ? entity.getSender().getId() : null)
                .senderName(senderName)
                .senderAvatar(entity.getSender() != null ? entity.getSender().getAvatarUrl() : null)
                .content(entity.getContent())
                .attachmentUrl(entity.getAttachmentUrl())
                .attachmentType(entity.getAttachmentType())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
