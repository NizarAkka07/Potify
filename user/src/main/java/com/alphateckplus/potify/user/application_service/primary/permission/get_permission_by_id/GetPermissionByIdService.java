package com.alphateckplus.potify.user.application_service.primary.permission.get_permission_by_id;

import com.alphateckplus.potify.user.domain.model.Permission;

/**
 * Port d'entree pour recuperer une permission par son id.
 */
public interface GetPermissionByIdService {

    Permission execute(String permissionId);
}
