package com.alphateckplus.potify.user.application_service.primary.permission.update_permission;

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
    public Permission execute(Permission permission) {
        Permission existingPermission = permissionRepositoryPort.findById(permission.getId())
            .orElseThrow(() -> new PermissionNotFoundException(permission.getId()));

        permissionRepositoryPort.findByCode(permission.getCode())
            .ifPresent(p -> {
                if (!p.getId().equals(existingPermission.getId())) {
                    throw new PermissionAlreadyExistsException(permission.getCode());
                }
            });

        existingPermission.setCode(permission.getCode());
        existingPermission.setDescription(permission.getDescription());
        return permissionRepositoryPort.save(existingPermission);
    }
}
