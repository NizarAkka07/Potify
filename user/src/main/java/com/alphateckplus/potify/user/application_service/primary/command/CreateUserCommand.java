package com.alphateckplus.potify.user.application_service.primary.command;

/**
 * Commande d'entree du cas d'usage de creation d'utilisateur.
 */
public record CreateUserCommand(
    String fullName,
    String email,
    String password
) {
}
