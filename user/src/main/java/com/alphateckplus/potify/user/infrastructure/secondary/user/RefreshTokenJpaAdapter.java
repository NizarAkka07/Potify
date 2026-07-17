package com.alphateckplus.potify.user.infrastructure.secondary.user;

import com.alphateckplus.potify.data_jpa.entity.user.RefreshTokenEntity;
import com.alphateckplus.potify.data_jpa.repository.user.RefreshTokenEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.user.application_service.secondary.user.RefreshTokenRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Adaptateur JPA pour la persistence des Refresh Tokens.
 */
@Transactional
public class RefreshTokenJpaAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenEntityRepository refreshTokenRepository;
    private final UserEntityRepository userEntityRepository;

    public RefreshTokenJpaAdapter(
            RefreshTokenEntityRepository refreshTokenRepository,
            UserEntityRepository userEntityRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public void save(String token, String userId, Instant expiryDate) {
        var userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + userId));

        // Supprimer l'ancien refresh token s'il existe déjà un token pour cet utilisateur
        deleteByUserId(userId);
        refreshTokenRepository.flush();

        var refreshTokenEntity = RefreshTokenEntity.builder()
                .token(token)
                .user(userEntity)
                .expiryDate(expiryDate)
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    public Optional<RefreshTokenInfo> findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(entity -> new RefreshTokenInfo(
                        entity.getToken(),
                        entity.getUser().getId(),
                        entity.getExpiryDate(),
                        entity.isRevoked()
                ));
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public void deleteByUserId(String userId) {
        refreshTokenRepository.findAll().stream()
                .filter(rt -> rt.getUser().getId().equals(userId))
                .forEach(refreshTokenRepository::delete);
    }

    @Override
    public void deleteExpiredTokens(Instant now) {
        refreshTokenRepository.findAll().stream()
                .filter(rt -> rt.getExpiryDate().isBefore(now) || rt.isRevoked())
                .forEach(refreshTokenRepository::delete);
    }
}
