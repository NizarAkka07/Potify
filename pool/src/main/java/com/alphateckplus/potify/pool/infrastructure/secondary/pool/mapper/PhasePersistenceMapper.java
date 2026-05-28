package com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper;

import com.alphateckplus.potify.data_jpa.entity.pool.PhaseEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PhaseStatus;
import com.alphateckplus.potify.pool.domain.model.Phase;
import org.springframework.stereotype.Component;

@Component
public class PhasePersistenceMapper {

    public PhaseEntity toEntity(Phase domain) {
        if (domain == null) return null;

        return PhaseEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .goalAmount(domain.getGoalAmount())
                .status(domain.getStatus() != null ? PhaseStatus.valueOf(domain.getStatus().name()) : PhaseStatus.ACTIVE)
                .build();
    }

    public Phase toDomain(PhaseEntity entity) {
        if (entity == null) return null;

        return Phase.builder()
                .id(entity.getId())
                .poolId(entity.getPool() != null ? entity.getPool().getId() : null)
                .title(entity.getTitle())
                .goalAmount(entity.getGoalAmount())
                .status(entity.getStatus() != null ? com.alphateckplus.potify.pool.domain.model.PhaseStatus.valueOf(entity.getStatus().name()) : com.alphateckplus.potify.pool.domain.model.PhaseStatus.ACTIVE)
                .build();
    }
}
