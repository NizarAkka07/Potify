package com.alphateckplus.potify.user.application_service.secondary.notification;

public interface NotificationPort {
    /**
     * Envoie un email de verification a l'utilisateur.
     * @param email L'adresse email de destination.
     * @param fullName Le nom de l'utilisateur.
     * @param token Le jeton de verification.
     */
    void sendVerificationEmail(String email, String fullName, String token);

    /**
     * Envoie un email de reinitialisation de mot de passe.
     * @param email L'adresse email de destination.
     * @param fullName Le nom de l'utilisateur.
     * @param token Le jeton de reinitialisation.
     */
    void sendPasswordResetEmail(String email, String fullName, String token);
}
