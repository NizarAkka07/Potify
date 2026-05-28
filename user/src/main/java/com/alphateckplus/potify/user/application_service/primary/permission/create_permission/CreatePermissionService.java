package com.alphateckplus.potify.user.application_service.primary.permission.create_permission;

import com.alphateckplus.potify.user.domain.model.Permission;

/**
 * Port d'entree pour creer une permission.
 */
public interface CreatePermissionService {

    Permission execute(Permission permission);
}
