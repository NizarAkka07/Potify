package com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper;

import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteStatus;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.domain.model.PoolStatus;
import com.alphateckplus.potify.pool.domain.model.PoolType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre le domaine Pool et l'entite PoolEntity.
 */
public class PoolPersistenceMapper {

    /**
     * Convertit un objet domaine vers son equivalent JPA.
     */
    public PoolEntity toEntity(Pool domain) {
        if (domain == null) return null;

        String invitedIds = null;
        if (domain.getInvitedUserIds() != null && !domain.getInvitedUserIds().isEmpty()) {
            invitedIds = String.join(",", domain.getInvitedUserIds());
        }

        return PoolEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .goalAmount(domain.getGoalAmount())
                .currentAmount(domain.getCurrentAmount())
                .status(CagnotteStatus.valueOf(domain.getStatus().name()))
                .type(domain.getType().name())
                .invitedUserIds(invitedIds)
                .imageUrl(domain.getImageUrl())
                .build();
    }

    /**
     * Convertit une entite JPA vers l'objet metier.
     */
    public Pool toDomain(PoolEntity entity) {
        if (entity == null) return null;

        Set<String> invitedIds = new HashSet<>();
        if (entity.getInvitedUserIds() != null && !entity.getInvitedUserIds().isBlank()) {
            invitedIds = Arrays.stream(entity.getInvitedUserIds().split(","))
                    .collect(Collectors.toSet());
        }

        return Pool.builder()
                .id(entity.getId())
                .ownerId(entity.getOwner() != null ? entity.getOwner().getId() : null)
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .title(entity.getTitle())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .goalAmount(entity.getGoalAmount())
                .currentAmount(entity.getCurrentAmount())
                .status(PoolStatus.valueOf(entity.getStatus().name()))
                .type(PoolType.valueOf(entity.getType()))
                .invitedUserIds(invitedIds)
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
