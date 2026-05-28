package com.alphateckplus.potify.user.application_service.primary.role.remove_permission_from_role;

import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.PermissionNotFoundException;
import com.alphateckplus.potify.user.domain.exception.RoleNotFoundException;
import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.domain.model.Permission;

public class DefaultRemovePermissionFromRoleService implements RemovePermissionFromRoleService {

    private final RoleRepositoryPort roleRepositoryPort;
    private final PermissionRepositoryPort permissionRepositoryPort;

    public DefaultRemovePermissionFromRoleService(
        RoleRepositoryPort roleRepositoryPort,
        PermissionRepositoryPort permissionRepositoryPort
    ) {
        this.roleRepositoryPort = roleRepositoryPort;
        this.permissionRepositoryPort = permissionRepositoryPort;
    }

    @Override
    public void execute(Role role, Permission permission) {
        roleRepositoryPort.findById(role.getId())
            .orElseThrow(() -> new RoleNotFoundException(role.getId()));

        permissionRepositoryPort.findById(permission.getId())
            .orElseThrow(() -> new PermissionNotFoundException(permission.getId()));

        roleRepositoryPort.removePermissionFromRole(role.getId(), permission.getId());
    }
}
