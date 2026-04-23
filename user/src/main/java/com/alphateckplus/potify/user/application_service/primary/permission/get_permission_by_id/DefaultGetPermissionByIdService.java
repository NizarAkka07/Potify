package com.alphateckplus.potify.user.application_service.primary.permission.get_permission_by_id;

import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.PermissionNotFoundException;
import com.alphateckplus.potify.user.domain.model.Permission;

/**
 * Implementation par defaut du cas d'usage de lecture permission par id.
 */
public class DefaultGetPermissionByIdService implements GetPermissionByIdService {

    private final PermissionRepositoryPort permissionRepositoryPort;

    public DefaultGetPermissionByIdService(PermissionRepositoryPort permissionRepositoryPort) {
        this.permissionRepositoryPort = permissionRepositoryPort;
    }

    @Override
    public Permission execute(String permissionId) {
        return permissionRepositoryPort.findById(permissionId)
            .orElseThrow(() -> new PermissionNotFoundException(permissionId));
    }
}
