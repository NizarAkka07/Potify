package com.alphateckplus.potify.user.application_service.primary.role.create_role;

import com.alphateckplus.potify.user.application_service.primary.command.CreateRoleCommand;
import com.alphateckplus.potify.user.domain.model.Role;

/**
 * Port d'entree pour creer un role.
 */
public interface CreateRoleService {

    Role execute(CreateRoleCommand command);
}
