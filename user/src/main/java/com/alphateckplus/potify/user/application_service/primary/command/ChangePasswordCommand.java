package com.alphateckplus.potify.user.application_service.primary.command;

/**
 * Commande pour changer le mot de passe.
 * Inclut l'ancien mot de passe pour verification (Securite accrue).
 */
public record ChangePasswordCommand(
    String userId,
    String oldPassword,
    String newPassword
) {}
