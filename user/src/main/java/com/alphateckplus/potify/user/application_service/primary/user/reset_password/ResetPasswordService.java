package com.alphateckplus.potify.user.application_service.primary.user.reset_password;

/**
 * Port d'entree pour la reinitialisation effective du mot de passe.
 */
public interface ResetPasswordService {
    /**
     * Reinitialise le mot de passe d'un utilisateur a partir d'un token valide.
     *
     * @param token       Le jeton de reinitialisation recu par email.
     * @param newPassword Le nouveau mot de passe en clair.
     * @return true si la reinitialisation a reussi, false si le token est invalide ou expire.
     */
    boolean execute(String token, String newPassword);
}
