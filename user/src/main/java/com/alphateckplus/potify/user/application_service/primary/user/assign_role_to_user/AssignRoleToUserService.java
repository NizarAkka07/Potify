package com.alphateckplus.potify.user.application_service.primary.user.assign_role_to_user;

import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.Role;

/**
 * Port d'entree pour affecter un role a un utilisateur.
 */
public interface AssignRoleToUserService {

    void execute(User user, Role role);
}
