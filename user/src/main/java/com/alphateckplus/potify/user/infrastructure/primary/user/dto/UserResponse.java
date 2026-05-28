package com.alphateckplus.potify.user.infrastructure.primary.user.dto;

import com.alphateckplus.potify.user.domain.model.UserStatus;
import java.time.Instant;

/**
 * DTO HTTP renvoye au client pour representer un utilisateur.
 */
public record UserResponse(
    String id,
    String fullName,
    String email,
    UserStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
