package com.alphateckplus.potify.pool.infrastructure.secondary.contribution.mapper;

import com.alphateckplus.potify.data_jpa.entity.pool.ContributionEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.ContributionStatus;
import com.alphateckplus.potify.pool.domain.model.Contribution;

/**
 * Mapper pour la conversion entre le domaine Contribution et l'entite ContributionEntity.
 */
public class ContributionPersistenceMapper {

    public ContributionEntity toEntity(Contribution domain) {
        if (domain == null) return null;

        return ContributionEntity.builder()
                .id(domain.getId())
                .contributorEmail(domain.getContributorEmail())
                .contributorName(domain.getContributorName())
                .amount(domain.getAmount())
                .message(domain.getMessage())
                .anonymous(domain.isAnonymous())
                .status(ContributionStatus.valueOf(domain.getStatus().name()))
                .paymentMethod(domain.getPaymentMethod())
                .build();
    }

    public Contribution toDomain(ContributionEntity entity) {
        if (entity == null) return null;

        return Contribution.builder()
                .id(entity.getId())
                .poolId(entity.getPool() != null ? entity.getPool().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .contributorEmail(entity.getContributorEmail())
                .contributorName(entity.getContributorName())
                .amount(entity.getAmount())
                .message(entity.getMessage())
                .anonymous(entity.isAnonymous())
                .status(com.alphateckplus.potify.pool.domain.model.ContributionStatus.valueOf(entity.getStatus().name()))
                .paymentMethod(entity.getPaymentMethod())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
