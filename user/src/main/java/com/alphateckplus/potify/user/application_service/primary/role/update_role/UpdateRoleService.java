package com.alphateckplus.potify.user.application_service.primary.role.update_role;

import com.alphateckplus.potify.user.domain.model.Role;

/**
 * Port d'entree pour mettre a jour un role.
 */
public interface UpdateRoleService {

    Role execute(Role role);
}
