package com.alphateckplus.potify.user.application_service.primary.command;

/**
 * Commande d'entree pour creer un role.
 */
public record CreateRoleCommand(
    String name,
    String description
) {
}
