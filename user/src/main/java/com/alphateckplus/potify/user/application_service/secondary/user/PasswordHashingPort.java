package com.alphateckplus.potify.user.application_service.secondary.user;

/**
 * Port pour le hachage des mots de passe.
 * Isole la logique de sécurité (Spring Security) de la couche applicative.
 */
public interface PasswordHashingPort {
    
    /**
     * Hache un mot de passe en texte clair.
     * 
     * @param rawPassword Le mot de passe en texte clair.
     * @return Le mot de passe haché.
     */
    String hash(String rawPassword);

    /**
     * Vérifie si un mot de passe en texte clair correspond à un mot de passe haché.
     * 
     * @param rawPassword Le mot de passe en texte clair.
     * @param encodedPassword Le mot de passe haché.
     * @return true si ça correspond, false sinon.
     */
    boolean matches(String rawPassword, String encodedPassword);
}
