package com.alphateckplus.potify.pool.infrastructure.primary.pool.dto;

import java.math.BigDecimal;

/**
 * Contrat d'entree pour la mise a jour d'une cagnotte.
 */
public record UpdatePoolRequest(
    String title,
    String description,
    BigDecimal goalAmount,
    String status,
    String videoUrl,
    String imageData, // Base64
    String imageContentType
) {}
