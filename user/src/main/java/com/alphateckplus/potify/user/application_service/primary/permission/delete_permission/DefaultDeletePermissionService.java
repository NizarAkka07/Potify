package com.alphateckplus.potify.user.application_service.primary.permission.delete_permission;

import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.PermissionNotFoundException;

/**
 * Implementation par defaut du cas d'usage de suppression permission.
 */
public class DefaultDeletePermissionService implements DeletePermissionService {

    private final PermissionRepositoryPort permissionRepositoryPort;

    public DefaultDeletePermissionService(PermissionRepositoryPort permissionRepositoryPort) {
        this.permissionRepositoryPort = permissionRepositoryPort;
    }

    @Override
    public void execute(String permissionId) {
        permissionRepositoryPort.findById(permissionId)
            .orElseThrow(() -> new PermissionNotFoundException(permissionId));

        permissionRepositoryPort.deleteById(permissionId);
    }
}
