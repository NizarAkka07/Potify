package com.alphateckplus.potify.user.application_service.primary.user.refresh_token;

import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.AuthResponse;

/**
 * Port d'entrée pour la gestion des refresh tokens (rafraîchissement de session et déconnexion).
 */
public interface RefreshTokenService {

    /**
     * Génère un refresh token pour un utilisateur donné et l'enregistre en base.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Le token généré sous forme de chaîne de caractères.
     */
    String createRefreshToken(String userId);

    /**
     * Valide un refresh token existant, génère un nouveau token d'accès,
     * et effectue la rotation du refresh token (génération d'un nouveau refresh token).
     *
     * @param refreshToken Le token de rafraîchissement à valider.
     * @return Les nouveaux tokens d'accès et de rafraîchissement sous forme de AuthResponse.
     */
    AuthResponse refreshSession(String refreshToken);

    /**
     * Révoque le refresh token de l'utilisateur pour le déconnecter.
     *
     * @param refreshToken Le token de rafraîchissement à révoquer.
     */
    void logout(String refreshToken);
}
