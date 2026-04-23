package com.alphateckplus.potify.user.infrastructure.primary.permission.mapper;

import com.alphateckplus.potify.user.application_service.primary.command.CreatePermissionCommand;
import com.alphateckplus.potify.user.application_service.primary.command.UpdatePermissionCommand;
import com.alphateckplus.potify.user.domain.model.Permission;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.CreatePermissionRequest;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.PermissionResponse;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.UpdatePermissionRequest;

/**
 * Mapper REST pour les objets Permission.
 */
public class PermissionRestMapper {

    public CreatePermissionCommand toCreateCommand(CreatePermissionRequest request) {
        return new CreatePermissionCommand(request.code(), request.description());
    }

    public UpdatePermissionCommand toUpdateCommand(String permissionId, UpdatePermissionRequest request) {
        return new UpdatePermissionCommand(permissionId, request.code(), request.description());
    }

    public PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(permission.getId(), permission.getCode(), permission.getDescription());
    }
}
