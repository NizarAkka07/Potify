package com.alphateckplus.potify.pool.infrastructure.secondary.contribution.mapper;

import com.alphateckplus.potify.data_jpa.entity.payment.ContributionEntity;
import com.alphateckplus.potify.data_jpa.entity.payment.ContributionStatus;
import com.alphateckplus.potify.pool.domain.model.Contribution;

/**
 * Mapper pour la conversion entre le domaine Contribution et l'entite ContributionEntity (partagee).
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
                .status(mapStatusToEntity(domain.getStatus()))
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
                .status(mapStatusToDomain(entity.getStatus()))
                .paymentMethod(entity.getPaymentMethod())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private ContributionStatus mapStatusToEntity(com.alphateckplus.potify.pool.domain.model.ContributionStatus domainStatus) {
        if (domainStatus == null) return null;
        return switch (domainStatus) {
            case EN_ATTENTE -> ContributionStatus.PENDING;
            case REUSSIE -> ContributionStatus.CONFIRMED;
            case REFUSEE -> ContributionStatus.CANCELLED;
            case REMBOURSEE -> ContributionStatus.REFUNDED;
        };
    }

    private com.alphateckplus.potify.pool.domain.model.ContributionStatus mapStatusToDomain(ContributionStatus entityStatus) {
        if (entityStatus == null) return null;
        return switch (entityStatus) {
            case PENDING -> com.alphateckplus.potify.pool.domain.model.ContributionStatus.EN_ATTENTE;
            case CONFIRMED -> com.alphateckplus.potify.pool.domain.model.ContributionStatus.REUSSIE;
            case CANCELLED -> com.alphateckplus.potify.pool.domain.model.ContributionStatus.REFUSEE;
            case REFUNDED -> com.alphateckplus.potify.pool.domain.model.ContributionStatus.REMBOURSEE;
        };
    }
}
