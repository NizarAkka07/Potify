package com.alphateckplus.potify.user.infrastructure.primary.permission.mapper;

import com.alphateckplus.potify.user.domain.model.Permission;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.CreatePermissionRequest;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.PermissionResponse;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.UpdatePermissionRequest;

/**
 * Mapper REST pour les objets Permission.
 */
public class PermissionRestMapper {

    public Permission toDomain(CreatePermissionRequest request) {
        return Permission.builder()
            .code(request.code())
            .description(request.description())
            .build();
    }

    public Permission toDomain(String permissionId, UpdatePermissionRequest request) {
        return Permission.builder()
            .id(permissionId)
            .code(request.code())
            .description(request.description())
            .build();
    }

    public PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(permission.getId(), permission.getCode(), permission.getDescription());
    }
}
