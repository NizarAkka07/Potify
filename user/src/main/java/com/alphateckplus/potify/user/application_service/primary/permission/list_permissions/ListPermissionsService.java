package com.alphateckplus.potify.user.application_service.primary.permission.list_permissions;

import com.alphateckplus.potify.user.domain.model.Permission;
import java.util.List;

/**
 * Port d'entree pour lister toutes les permissions.
 */
public interface ListPermissionsService {

    List<Permission> execute();
}
