package com.alphateckplus.potify.user.application_service.primary.permission.list_permissions;

import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.domain.model.Permission;
import java.util.List;

/**
 * Implementation par defaut du cas d'usage de listing des permissions.
 */
public class DefaultListPermissionsService implements ListPermissionsService {

    private final PermissionRepositoryPort permissionRepositoryPort;

    public DefaultListPermissionsService(PermissionRepositoryPort permissionRepositoryPort) {
        this.permissionRepositoryPort = permissionRepositoryPort;
    }

    @Override
    public List<Permission> execute() {
        return permissionRepositoryPort.findAll();
    }
}
