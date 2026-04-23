package com.alphateckplus.potify.user.infrastructure.primary.role.dto;

/**
 * DTO HTTP de sortie pour un role.
 */
public record RoleResponse(
    String id,
    String name,
    String description
) {
}
