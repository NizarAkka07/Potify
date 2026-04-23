package com.alphateckplus.potify.user.application_service.primary.command;

/**
 * Commande d'entree pour creer une permission.
 */
public record CreatePermissionCommand(
    String code,
    String description
) {
}
