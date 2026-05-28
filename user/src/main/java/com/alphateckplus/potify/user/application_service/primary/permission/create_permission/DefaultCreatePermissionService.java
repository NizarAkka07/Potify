package com.alphateckplus.potify.user.application_service.primary.permission.create_permission;

import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.PermissionAlreadyExistsException;
import com.alphateckplus.potify.user.domain.model.Permission;

/**
 * Implementation par defaut du cas d'usage de creation permission.
 */
public class DefaultCreatePermissionService implements CreatePermissionService {

    private final PermissionRepositoryPort permissionRepositoryPort;

    public DefaultCreatePermissionService(PermissionRepositoryPort permissionRepositoryPort) {
        this.permissionRepositoryPort = permissionRepositoryPort;
    }

    @Override
    public Permission execute(Permission permission) {
        if (permissionRepositoryPort.findByCode(permission.getCode()).isPresent()) {
            throw new PermissionAlreadyExistsException(permission.getCode());
        }

        return permissionRepositoryPort.save(permission);
    }
}
