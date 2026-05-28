package com.alphateckplus.potify.user.infrastructure.primary.role.mapper;

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

    public Role toDomain(CreateRoleRequest request) {
        return Role.builder()
            .name(request.name())
            .description(request.description())
            .build();
    }

    public Role toDomain(String roleId, UpdateRoleRequest request) {
        return Role.builder()
            .id(roleId)
            .name(request.name())
            .description(request.description())
            .build();
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
