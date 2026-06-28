package com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper;

import com.alphateckplus.potify.data_jpa.entity.pool.MessageEntity;
import com.alphateckplus.potify.pool.domain.model.Message;
import com.alphateckplus.potify.pool.domain.model.Reaction;

public class MessagePersistenceMapper {

    public MessageEntity toEntity(Message domain) {
        if (domain == null) return null;
        return MessageEntity.builder()
                .id(domain.getId())
                .content(domain.getContent())
                .isPublic(domain.isPublic())
                .build();
    }

    public Message toDomain(MessageEntity entity) {
        if (entity == null) return null;
        
        java.util.List<Reaction> reactions = entity.getReactions() != null ? entity.getReactions().stream()
                .map(r -> Reaction.builder()
                        .id(r.getId())
                        .messageId(entity.getId())
                        .userId(r.getUser().getId())
                        .reactionType(r.getReactionType())
                        .build())
                .collect(java.util.stream.Collectors.toList()) : new java.util.ArrayList<>();
        
        java.util.List<com.alphateckplus.potify.pool.domain.model.MessageReport> reports = entity.getReports() != null ? entity.getReports().stream()
                .map(rep -> com.alphateckplus.potify.pool.domain.model.MessageReport.builder()
                        .id(rep.getId())
                        .messageId(entity.getId())
                        .userId(rep.getUser() != null ? rep.getUser().getId() : null)
                        .userName(rep.getUser() != null ? rep.getUser().getFullName() : null)
                        .reason(rep.getReason())
                        .createdAt(rep.getCreatedAt())
                        .build())
                .collect(java.util.stream.Collectors.toList()) : new java.util.ArrayList<>();
        
        return Message.builder()
                .id(entity.getId())
                .poolId(entity.getPool() != null ? entity.getPool().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .userName(entity.getUser() != null ? entity.getUser().getFullName() : null)
                .content(entity.getContent())
                .isPublic(entity.isPublic())
                .reactions(reactions)
                .reports(reports)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
