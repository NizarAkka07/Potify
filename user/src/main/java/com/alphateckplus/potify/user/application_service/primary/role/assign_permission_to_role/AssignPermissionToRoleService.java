package com.alphateckplus.potify.user.application_service.primary.role.assign_permission_to_role;

import com.alphateckplus.potify.user.application_service.primary.command.AssignPermissionToRoleCommand;

/**
 * Port d'entree pour affecter une permission a un role.
 */
public interface AssignPermissionToRoleService {

    void execute(AssignPermissionToRoleCommand command);
}
