package com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper;

import com.alphateckplus.potify.data_jpa.entity.pool.MessageEntity;
import com.alphateckplus.potify.pool.domain.model.Message;

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
        return Message.builder()
                .id(entity.getId())
                .poolId(entity.getPool() != null ? entity.getPool().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .userName(entity.getUser() != null ? entity.getUser().getFullName() : null)
                .content(entity.getContent())
                .isPublic(entity.isPublic())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
