package com.alphateckplus.potify.user.application_service.primary.role.assign_permission_to_role;

import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.PermissionNotFoundException;
import com.alphateckplus.potify.user.domain.exception.RoleNotFoundException;
import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.domain.model.Permission;

/**
 * Implementation par defaut du cas d'usage d'affectation permission -> role.
 */
public class DefaultAssignPermissionToRoleService implements AssignPermissionToRoleService {

    private final RoleRepositoryPort roleRepositoryPort;
    private final PermissionRepositoryPort permissionRepositoryPort;

    public DefaultAssignPermissionToRoleService(
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

        roleRepositoryPort.addPermissionToRole(role.getId(), permission.getId());
    }
}
