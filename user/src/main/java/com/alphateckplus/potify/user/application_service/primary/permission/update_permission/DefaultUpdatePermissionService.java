package com.alphateckplus.potify.user.application_service.primary.permission.update_permission;

import com.alphateckplus.potify.user.application_service.primary.command.UpdatePermissionCommand;
import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.PermissionAlreadyExistsException;
import com.alphateckplus.potify.user.domain.exception.PermissionNotFoundException;
import com.alphateckplus.potify.user.domain.model.Permission;

/**
 * Implementation par defaut du cas d'usage de mise a jour permission.
 */
public class DefaultUpdatePermissionService implements UpdatePermissionService {

    private final PermissionRepositoryPort permissionRepositoryPort;

    public DefaultUpdatePermissionService(PermissionRepositoryPort permissionRepositoryPort) {
        this.permissionRepositoryPort = permissionRepositoryPort;
    }

    @Override
    public Permission execute(UpdatePermissionCommand command) {
        Permission existingPermission = permissionRepositoryPort.findById(command.permissionId())
            .orElseThrow(() -> new PermissionNotFoundException(command.permissionId()));

        permissionRepositoryPort.findByCode(command.code())
            .ifPresent(permission -> {
                if (!permission.getId().equals(existingPermission.getId())) {
                    throw new PermissionAlreadyExistsException(command.code());
                }
            });

        existingPermission.setCode(command.code());
        existingPermission.setDescription(command.description());
        return permissionRepositoryPort.save(existingPermission);
    }
}
