package com.alphateckplus.potify.user.application_service.primary.permission.delete_permission;

/**
 * Port d'entree pour supprimer une permission.
 */
public interface DeletePermissionService {

    void execute(String permissionId);
}
