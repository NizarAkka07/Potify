package com.alphateckplus.potify.user.infrastructure.primary.dto;

import java.time.Instant;

/**
 * Format d'erreur standard pour les reponses HTTP.
 */
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path
) {
}
