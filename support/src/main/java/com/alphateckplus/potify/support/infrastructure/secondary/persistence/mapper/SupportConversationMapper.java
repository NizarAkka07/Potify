package com.alphateckplus.potify.support.infrastructure.secondary.persistence.mapper;

import com.alphateckplus.potify.data_jpa.entity.support.SupportConversationEntity;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import org.springframework.stereotype.Component;

@Component
public class SupportConversationMapper {

    public SupportConversation toDomain(SupportConversationEntity entity) {
        if (entity == null) return null;
        return SupportConversation.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .userName(entity.getUser() != null ? entity.getUser().getFullName() : "Invité")
                .userEmail(entity.getUser() != null ? entity.getUser().getEmail() : null)
                .userAvatar(entity.getUser() != null ? entity.getUser().getAvatarUrl() : null)
                .assignedAdminId(entity.getAssignedAdmin() != null ? entity.getAssignedAdmin().getId() : null)
                .assignedAdminName(entity.getAssignedAdmin() != null ? entity.getAssignedAdmin().getFullName() : null)
                .status(entity.getStatus())
                .subject(entity.getSubject())
                .lastMessageAt(entity.getLastMessageAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
