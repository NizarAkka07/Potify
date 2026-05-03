package com.alphateckplus.potify.pool.application_service.primary.command;

import java.math.BigDecimal;
import lombok.Builder;

/**
 * Commande pour creer une contribution.
 */
@Builder
public record CreateContributionCommand(
    String poolId,
    String userId,
    String contributorEmail,
    String contributorName,
    BigDecimal amount,
    String message,
    boolean anonymous,
    String paymentMethod
) {}
