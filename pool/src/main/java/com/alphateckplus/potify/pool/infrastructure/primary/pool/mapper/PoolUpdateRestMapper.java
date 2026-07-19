package com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper;

import com.alphateckplus.potify.pool.domain.model.PoolUpdate;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolUpdateRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolUpdateResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir les requêtes/réponses REST pour les actualités de cagnottes.
 */
@Component
public class PoolUpdateRestMapper {

    public PoolUpdate toDomain(PoolUpdateRequest request) {
        if (request == null) return null;
        return PoolUpdate.builder()
                .title(request.title())
                .content(request.content())
                .imageUrl(request.imageUrl())
                .videoUrl(request.videoUrl())
                .build();
    }

    public PoolUpdateResponse toResponse(PoolUpdate domain) {
        if (domain == null) return null;
        return new PoolUpdateResponse(
                domain.getId(),
                domain.getPoolId(),
                domain.getTitle(),
                domain.getContent(),
                domain.getImageUrl(),
                domain.getVideoUrl(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
