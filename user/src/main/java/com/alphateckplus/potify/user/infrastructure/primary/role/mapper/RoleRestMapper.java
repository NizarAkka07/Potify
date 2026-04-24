package com.alphateckplus.potify.user.infrastructure.primary.role.mapper;

import com.alphateckplus.potify.user.application_service.primary.command.CreateRoleCommand;
import com.alphateckplus.potify.user.application_service.primary.command.UpdateRoleCommand;
import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.infrastructure.primary.role.dto.CreateRoleRequest;
import com.alphateckplus.potify.user.infrastructure.primary.role.dto.RoleResponse;
import com.alphateckplus.potify.user.infrastructure.primary.role.dto.UpdateRoleRequest;

import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.PermissionResponse;
import java.util.List;

/**
 * Mapper REST pour les objets Role.
 */
public class RoleRestMapper {

    public CreateRoleCommand toCreateCommand(CreateRoleRequest request) {
        return new CreateRoleCommand(request.name(), request.description());
    }

    public UpdateRoleCommand toUpdateCommand(String roleId, UpdateRoleRequest request) {
        return new UpdateRoleCommand(roleId, request.name(), request.description());
    }

    public RoleResponse toResponse(Role role) {
        List<PermissionResponse> permissionResponses = role.getPermissions() != null
            ? role.getPermissions().stream()
                .map(p -> new PermissionResponse(p.getId(), p.getCode(), p.getDescription()))
                .toList()
            : List.of();
            
        return new RoleResponse(role.getId(), role.getName(), role.getDescription(), permissionResponses);
    }
}
