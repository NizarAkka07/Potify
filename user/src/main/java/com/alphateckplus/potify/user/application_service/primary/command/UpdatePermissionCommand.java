package com.alphateckplus.potify.user.application_service.primary.command;

/**
 * Commande d'entree pour modifier une permission.
 */
public record UpdatePermissionCommand(
    String permissionId,
    String code,
    String description
) {
}
