package com.alphateckplus.potify.user.infrastructure.primary.role.dto;

import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.PermissionResponse;
import java.util.List;

/**
 * DTO HTTP de sortie pour un role.
 */
public record RoleResponse(
    String id,
    String name,
    String description,
    List<PermissionResponse> permissions
) {
}
