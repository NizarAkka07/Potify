package com.alphateckplus.potify.user.application_service.secondary.user;

import java.time.Instant;
import java.util.Optional;

/**
 * Port de sortie pour la persistance des Refresh Tokens.
 */
public interface RefreshTokenRepositoryPort {

    void save(String token, String userId, Instant expiryDate);

    Optional<RefreshTokenInfo> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUserId(String userId);

    void deleteExpiredTokens(Instant now);

    /**
     * Record immuable représentant un Refresh Token.
     */
    record RefreshTokenInfo(String token, String userId, Instant expiryDate, boolean revoked) {}
}
