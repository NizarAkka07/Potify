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
    String title,
    String description,
    String category,
    BigDecimal goalAmount,
    BigDecimal currentAmount,
    PoolStatus status,
    PoolType type,
    Set<String> invitedUserIds,
    String imageUrl,
    BigDecimal progressPercentage,
    Instant createdAt,
    Instant updatedAt
) {}
