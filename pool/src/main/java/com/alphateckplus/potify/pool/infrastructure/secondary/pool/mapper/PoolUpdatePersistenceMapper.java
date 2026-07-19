package com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolUpdateEntity;
import com.alphateckplus.potify.pool.domain.model.PoolUpdate;

/**
 * Mapper pour convertir entre l'entité JPA PoolUpdateEntity et le modèle de domaine PoolUpdate.
 */
public class PoolUpdatePersistenceMapper {

    public PoolUpdateEntity toEntity(PoolUpdate domain) {
        if (domain == null) return null;
        return PoolUpdateEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .content(domain.getContent())
                .imageUrl(domain.getImageUrl())
                .videoUrl(domain.getVideoUrl())
                .build();
    }

    public PoolUpdate toDomain(PoolUpdateEntity entity) {
        if (entity == null) return null;
        return PoolUpdate.builder()
                .id(entity.getId())
                .poolId(entity.getPool() != null ? entity.getPool().getId() : null)
                .title(entity.getTitle())
                .content(entity.getContent())
                .imageUrl(entity.getImageUrl())
                .videoUrl(entity.getVideoUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
