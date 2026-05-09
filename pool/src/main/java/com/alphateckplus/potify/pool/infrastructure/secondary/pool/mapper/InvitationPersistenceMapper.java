package com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolInvitationEntity;
import com.alphateckplus.potify.pool.domain.model.Invitation;

public class InvitationPersistenceMapper {

    public PoolInvitationEntity toEntity(Invitation domain) {
        if (domain == null) return null;
        return PoolInvitationEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .status(domain.getStatus())
                .token(domain.getToken())
                .build();
    }

    public Invitation toDomain(PoolInvitationEntity entity) {
        if (entity == null) return null;
        return Invitation.builder()
                .id(entity.getId())
                .poolId(entity.getPool() != null ? entity.getPool().getId() : null)
                .email(entity.getEmail())
                .status(entity.getStatus())
                .token(entity.getToken())
                .build();
    }
}
