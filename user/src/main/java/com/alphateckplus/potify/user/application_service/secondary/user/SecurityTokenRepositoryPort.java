package com.alphateckplus.potify.user.application_service.secondary.user;

import java.util.Optional;

/**
 * Port de sortie pour la persistence des jetons de securite
 * (verification email, reset password).
 */
public interface SecurityTokenRepositoryPort {

    void save(String token, String type, String userId, java.time.LocalDateTime expiryDate);

    Optional<SecurityTokenInfo> findByToken(String token);

    void deleteByToken(String token);

    void deleteExpiredTokens(java.time.LocalDateTime now);

    /**
     * Record immuable pour transporter les informations d'un jeton
     * sans exposer l'entite JPA au domaine.
     */
    record SecurityTokenInfo(String token, String type, String userId, java.time.LocalDateTime expiryDate) {}
}
