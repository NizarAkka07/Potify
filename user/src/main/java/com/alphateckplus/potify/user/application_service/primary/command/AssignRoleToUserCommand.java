package com.alphateckplus.potify.user.application_service.primary.command;

/**
 * Commande d'entree pour affecter un role a un utilisateur.
 */
public record AssignRoleToUserCommand(
    String userId,
    String roleId
) {
}
