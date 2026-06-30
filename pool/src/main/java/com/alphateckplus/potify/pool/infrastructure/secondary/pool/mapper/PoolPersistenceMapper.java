package com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper;

import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteStatus;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PhaseEntity;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.domain.model.PoolStatus;
import com.alphateckplus.potify.pool.domain.model.PoolType;
import com.alphateckplus.potify.pool.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre le domaine Pool et l'entite PoolEntity.
 */
@RequiredArgsConstructor
public class PoolPersistenceMapper {

    private final WalletPersistenceMapper walletPersistenceMapper;
    private final InvitationPersistenceMapper invitationPersistenceMapper;
    private final PhasePersistenceMapper phasePersistenceMapper;

    /**
     * Convertit un objet domaine vers son equivalent JPA.
     */
    public PoolEntity toEntity(Pool domain) {
        if (domain == null) return null;

        String invitedIds = null;
        if (domain.getInvitedUserIds() != null && !domain.getInvitedUserIds().isEmpty()) {
            invitedIds = String.join(",", domain.getInvitedUserIds());
        }

        PoolEntity entity = PoolEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .goalAmount(domain.getGoalAmount())
                .currentAmount(domain.getCurrentAmount())
                .status(domain.getStatus() != null ? CagnotteStatus.valueOf(domain.getStatus().name()) : CagnotteStatus.BROUILLON)
                .type(domain.getType() != null ? domain.getType().name() : "PUBLIC")
                .invitedUserIds(invitedIds)
                .imageContent(domain.getImageContent())
                .imageContentType(domain.getImageContentType())
                .videoContent(domain.getVideoContent())
                .videoContentType(domain.getVideoContentType())
                .videoUrl(domain.getVideoUrl())
                .hasDeadline(domain.getHasDeadline() != null ? domain.getHasDeadline() : false)
                .deadlineDate(domain.getDeadlineDate())
                .fees(domain.getFees())
                .build();

        if (domain.getPhases() != null) {
            java.util.List<PhaseEntity> phaseEntities = domain.getPhases().stream()
                    .map(phasePersistenceMapper::toEntity)
                    .collect(Collectors.toList());
            phaseEntities.forEach(p -> p.setPool(entity));
            entity.setPhases(phaseEntities);
        }

        return entity;
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

        java.util.List<com.alphateckplus.potify.pool.domain.model.PoolReport> reports = entity.getReports() != null ? entity.getReports().stream()
                .map(rep -> com.alphateckplus.potify.pool.domain.model.PoolReport.builder()
                        .id(rep.getId())
                        .poolId(entity.getId())
                        .userId(rep.getUser() != null ? rep.getUser().getId() : null)
                        .userName(rep.getUser() != null ? rep.getUser().getFullName() : null)
                        .reason(rep.getReason())
                        .createdAt(rep.getCreatedAt())
                        .build())
                .collect(Collectors.toList()) : new java.util.ArrayList<>();

        return Pool.builder()
                .id(entity.getId())
                .ownerId(entity.getOwner() != null ? entity.getOwner().getId() : null)
                .ownerName(entity.getOwner() != null ? entity.getOwner().getFullName() : null)
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .title(entity.getTitle())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .goalAmount(entity.getGoalAmount())
                .currentAmount(entity.getCurrentAmount())
                .status(entity.getStatus() != null ? PoolStatus.valueOf(entity.getStatus().name()) : PoolStatus.PUBLIEE)
                .type(entity.getType() != null ? PoolType.valueOf(entity.getType()) : PoolType.PUBLIC)
                .invitedUserIds(invitedIds)
                .imageContent(entity.getImageContent())
                .imageContentType(entity.getImageContentType())
                .videoContent(entity.getVideoContent())
                .videoContentType(entity.getVideoContentType())
                .videoUrl(entity.getVideoUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .wallet(walletPersistenceMapper.toDomain(entity.getWallet()))
                .invitations(entity.getInvitations() != null ? 
                        entity.getInvitations().stream().map(invitationPersistenceMapper::toDomain).collect(Collectors.toList()) : 
                        null)
                .children(entity.getChildren() != null ?
                        entity.getChildren().stream().map(this::toDomainShort).collect(Collectors.toList()) :
                        new java.util.ArrayList<>())
                .hasDeadline(entity.getHasDeadline() != null ? entity.getHasDeadline() : false)
                .deadlineDate(entity.getDeadlineDate())
                .phases(entity.getPhases() != null ?
                        entity.getPhases().stream().map(phasePersistenceMapper::toDomain).collect(Collectors.toList()) :
                        new java.util.ArrayList<>())
                .fees(entity.getFees())
                .reports(reports)
                .build();
    }

    /**
     * Version courte de toDomain pour eviter la recursion infinie sur les enfants.
     */
    private Pool toDomainShort(PoolEntity entity) {
        if (entity == null) return null;
        return Pool.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .currentAmount(entity.getCurrentAmount())
                .goalAmount(entity.getGoalAmount())
                .status(entity.getStatus() != null ? PoolStatus.valueOf(entity.getStatus().name()) : PoolStatus.PUBLIEE)
                .hasDeadline(entity.getHasDeadline() != null ? entity.getHasDeadline() : false)
                .deadlineDate(entity.getDeadlineDate())
                .phases(entity.getPhases() != null ?
                        entity.getPhases().stream().map(phasePersistenceMapper::toDomain).collect(Collectors.toList()) :
                        new java.util.ArrayList<>())
                .fees(entity.getFees())
                .build();
    }
}
