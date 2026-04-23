package com.alphateckplus.potify.user.application_service.primary.role.assign_permission_to_role;

import com.alphateckplus.potify.user.application_service.primary.command.AssignPermissionToRoleCommand;
import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.PermissionNotFoundException;
import com.alphateckplus.potify.user.domain.exception.RoleNotFoundException;

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
    public void execute(AssignPermissionToRoleCommand command) {
        roleRepositoryPort.findById(command.roleId())
            .orElseThrow(() -> new RoleNotFoundException(command.roleId()));

        permissionRepositoryPort.findById(command.permissionId())
            .orElseThrow(() -> new PermissionNotFoundException(command.permissionId()));

        roleRepositoryPort.addPermissionToRole(command.roleId(), command.permissionId());
    }
}
