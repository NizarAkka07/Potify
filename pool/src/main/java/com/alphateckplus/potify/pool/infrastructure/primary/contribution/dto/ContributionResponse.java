package com.alphateckplus.potify.pool.infrastructure.primary.contribution.dto;

import com.alphateckplus.potify.pool.domain.model.ContributionStatus;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Contrat de sortie pour une contribution.
 */
public record ContributionResponse(
    String id,
    String poolId,
    String userId,
    String contributorEmail,
    String contributorName,
    BigDecimal amount,
    String message,
    boolean anonymous,
    ContributionStatus status,
    String paymentMethod,
    Instant createdAt
) {}
