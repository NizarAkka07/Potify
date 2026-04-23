package com.alphateckplus.potify.user.application_service.primary.role.get_role_by_id;

import com.alphateckplus.potify.user.domain.model.Role;

/**
 * Port d'entree pour recuperer un role par son id.
 */
public interface GetRoleByIdService {

    Role execute(String roleId);
}
