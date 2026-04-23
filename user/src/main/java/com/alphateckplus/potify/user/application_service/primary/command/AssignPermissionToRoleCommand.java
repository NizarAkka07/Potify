package com.alphateckplus.potify.user.application_service.primary.command;

/**
 * Commande d'entree pour affecter une permission a un role.
 */
public record AssignPermissionToRoleCommand(
    String roleId,
    String permissionId
) {
}
