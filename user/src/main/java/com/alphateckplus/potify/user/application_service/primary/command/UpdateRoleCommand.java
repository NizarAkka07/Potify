package com.alphateckplus.potify.user.application_service.primary.command;

/**
 * Commande d'entree pour modifier un role.
 */
public record UpdateRoleCommand(
    String roleId,
    String name,
    String description
) {
}
