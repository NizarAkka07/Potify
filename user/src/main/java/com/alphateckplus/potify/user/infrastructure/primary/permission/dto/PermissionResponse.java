package com.alphateckplus.potify.user.infrastructure.primary.permission.dto;

/**
 * DTO HTTP de sortie pour une permission.
 */
public record PermissionResponse(
    String id,
    String code,
    String description
) {
}
