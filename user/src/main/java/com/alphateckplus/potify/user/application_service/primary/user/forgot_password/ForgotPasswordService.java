package com.alphateckplus.potify.user.application_service.primary.user.forgot_password;

/**
 * Port d'entree pour la demande de reinitialisation de mot de passe.
 */
public interface ForgotPasswordService {
    /**
     * Envoie un email de reinitialisation si l'adresse email existe.
     * Ne leve jamais d'exception pour ne pas reveler l'existence d'un compte.
     *
     * @param email L'adresse email de l'utilisateur.
     */
    void execute(String email);
}
