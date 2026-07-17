package com.alphateckplus.potify.user.application_service.primary.user.refresh_token;

import com.alphateckplus.potify.user.application_service.secondary.user.RefreshTokenRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.AuthResponse;
import com.alphateckplus.potify.user.infrastructure.primary.security.JwtUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation par défaut de la gestion des refresh tokens.
 * Effectue la rotation du refresh token (nouveau refresh token à chaque rafraîchissement)
 * pour éviter les attaques par rejeu de jeton.
 */
public class DefaultRefreshTokenService implements RefreshTokenService {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final long expirationMs;

    public DefaultRefreshTokenService(
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            JwtUtils jwtUtils,
            UserDetailsService userDetailsService,
            long expirationMs) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.expirationMs = expirationMs;
    }

    @Override
    public String createRefreshToken(String userId) {
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(expirationMs);
        refreshTokenRepositoryPort.save(token, userId, expiryDate);
        return token;
    }

    @Override
    public AuthResponse refreshSession(String token) {
        // 1. Chercher le token
        Optional<RefreshTokenRepositoryPort.RefreshTokenInfo> tokenInfoOpt = refreshTokenRepositoryPort.findByToken(token);

        if (tokenInfoOpt.isEmpty()) {
            throw new IllegalArgumentException("Refresh token invalide.");
        }

        RefreshTokenRepositoryPort.RefreshTokenInfo tokenInfo = tokenInfoOpt.get();

        // 2. Vérifier si révoqué ou expiré
        if (tokenInfo.revoked()) {
            refreshTokenRepositoryPort.deleteByToken(token);
            throw new IllegalArgumentException("Ce Refresh token a été révoqué.");
        }

        if (tokenInfo.expiryDate().isBefore(Instant.now())) {
            refreshTokenRepositoryPort.deleteByToken(token);
            throw new IllegalArgumentException("Le Refresh token est expiré. Veuillez vous reconnecter.");
        }

        // 3. Récupérer l'utilisateur
        Optional<User> userOpt = userRepositoryPort.findById(tokenInfo.userId());
        if (userOpt.isEmpty()) {
            refreshTokenRepositoryPort.deleteByToken(token);
            throw new IllegalArgumentException("Utilisateur associé introuvable.");
        }

        User user = userOpt.get();

        // 4. Charger les détails de sécurité de l'utilisateur
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        // 5. Générer le nouveau access token JWT
        String newAccessToken = jwtUtils.generateToken(userDetails);

        // 6. Rotation du refresh token : générer un nouveau token, l'enregistrer et supprimer l'ancien
        String newRefreshToken = UUID.randomUUID().toString();
        Instant newExpiryDate = Instant.now().plusMillis(expirationMs);
        
        // Sauvegarder le nouveau refresh token (le save supprime aussi l'ancien via l'adapter)
        refreshTokenRepositoryPort.save(newRefreshToken, user.getId(), newExpiryDate);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .id(user.getId())
                .email(user.getEmail())
                .roles(roles)
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    @Override
    public void logout(String token) {
        refreshTokenRepositoryPort.deleteByToken(token);
    }
}
