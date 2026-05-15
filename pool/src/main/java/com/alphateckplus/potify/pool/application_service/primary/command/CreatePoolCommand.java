package com.alphateckplus.potify.pool.application_service.primary.command;

import com.alphateckplus.potify.pool.domain.model.PoolType;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Builder;

/**
 * Commande pour la creation d'une nouvelle cagnotte.
 */
@Builder
public record CreatePoolCommand(
    String ownerId,
    String title,
    String description,
    String category,
    BigDecimal goalAmount,
    PoolType type,
    Set<String> invitedUserIds,
    byte[] imageContent,
    String imageContentType,
    String videoUrl,
    String parentId
) {}
