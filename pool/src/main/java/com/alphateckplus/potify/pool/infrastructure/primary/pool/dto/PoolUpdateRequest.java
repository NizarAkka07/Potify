package com.alphateckplus.potify.pool.infrastructure.primary.pool.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de requête pour créer ou modifier une mise à jour de cagnotte.
 */
public record PoolUpdateRequest(
    @NotBlank(message = "Le titre est obligatoire") String title,
    @NotBlank(message = "Le contenu est obligatoire") String content,
    String imageUrl,
    String videoUrl
) {}
