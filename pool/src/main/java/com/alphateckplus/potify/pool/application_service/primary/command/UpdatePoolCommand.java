package com.alphateckplus.potify.pool.application_service.primary.command;

import com.alphateckplus.potify.pool.domain.model.PoolStatus;
import java.math.BigDecimal;
import lombok.Builder;

/**
 * Commande pour mettre a jour une cagnotte existante.
 */
@Builder
public record UpdatePoolCommand(
    String id,
    String title,
    String description,
    BigDecimal goalAmount,
    PoolStatus status
) {}
