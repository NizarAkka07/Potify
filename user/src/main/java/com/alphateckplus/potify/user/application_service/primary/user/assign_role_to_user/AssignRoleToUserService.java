package com.alphateckplus.potify.user.application_service.primary.user.assign_role_to_user;

import com.alphateckplus.potify.user.application_service.primary.command.AssignRoleToUserCommand;

/**
 * Port d'entree pour affecter un role a un utilisateur.
 */
public interface AssignRoleToUserService {

    void execute(AssignRoleToUserCommand command);
}
