package com.alphateckplus.potify.user.application_service.primary.permission.update_permission;

import com.alphateckplus.potify.user.application_service.primary.command.UpdatePermissionCommand;
import com.alphateckplus.potify.user.domain.model.Permission;

/**
 * Port d'entree pour mettre a jour une permission.
 */
public interface UpdatePermissionService {

    Permission execute(UpdatePermissionCommand command);
}
