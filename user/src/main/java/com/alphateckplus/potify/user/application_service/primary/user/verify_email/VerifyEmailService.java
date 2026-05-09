package com.alphateckplus.potify.user.application_service.primary.user.verify_email;

public interface VerifyEmailService {
    /**
     * Verifie l'email d'un utilisateur via son jeton.
     * @param token Le jeton de verification.
     * @return true si la verification a réussi.
     */
    boolean execute(String token);
}
