package com.alphateckplus.potify.user.application_service.primary.command;

import com.alphateckplus.potify.user.domain.model.UserStatus;

/**
 * Commande d'entree pour modifier les informations d'un utilisateur.
 */
public record UpdateUserCommand(
    String userId,
    String fullName,
    String email,
    String password,
    UserStatus status
) {
}
