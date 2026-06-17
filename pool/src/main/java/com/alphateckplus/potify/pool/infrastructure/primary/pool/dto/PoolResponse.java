package com.alphateckplus.potify.pool.infrastructure.primary.pool.dto;

import com.alphateckplus.potify.pool.domain.model.PoolStatus;
import com.alphateckplus.potify.pool.domain.model.PoolType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

/**
 * Contrat de sortie pour une cagnotte.
 */
public record PoolResponse(
    String id,
    String ownerId,
    String ownerName,
    String parentId,
    String title,
    String description,
    String category,
    BigDecimal goalAmount,
    BigDecimal currentAmount,
    PoolStatus status,
    PoolType type,
    Set<String> invitedUserIds,
    String imageUrl,
    String videoUrl,
    BigDecimal progressPercentage,
    BigDecimal availableBalance,
    BigDecimal pendingBalance,
    java.util.List<PhaseResponse> phases,
    java.util.List<PoolResponse> subPools,
    Boolean hasDeadline,
    java.time.LocalDateTime deadlineDate,
    Instant createdAt,
    Instant updatedAt,
    BigDecimal fees
) {}
