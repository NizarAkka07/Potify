package com.alphateckplus.potify.user.infrastructure.secondary.user;

import com.alphateckplus.potify.data_jpa.entity.user.SecurityTokenEntity;
import com.alphateckplus.potify.data_jpa.repository.user.SecurityTokenEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.user.application_service.secondary.user.SecurityTokenRepositoryPort;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Adaptateur JPA pour la persistence des SecurityTokens.
 */
public class SecurityTokenJpaAdapter implements SecurityTokenRepositoryPort {

    private final SecurityTokenEntityRepository securityTokenRepository;
    private final UserEntityRepository userEntityRepository;

    public SecurityTokenJpaAdapter(
            SecurityTokenEntityRepository securityTokenRepository,
            UserEntityRepository userEntityRepository) {
        this.securityTokenRepository = securityTokenRepository;
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public void save(String token, String type, String userId, LocalDateTime expiryDate) {
        var userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + userId));

        var tokenEntity = SecurityTokenEntity.builder()
                .token(token)
                .type(SecurityTokenEntity.SecurityTokenType.valueOf(type))
                .user(userEntity)
                .expiryDate(expiryDate)
                .build();

        securityTokenRepository.save(tokenEntity);
    }

    @Override
    public Optional<SecurityTokenInfo> findByToken(String token) {
        return securityTokenRepository.findByToken(token)
                .map(entity -> new SecurityTokenInfo(
                        entity.getToken(),
                        entity.getType().name(),
                        entity.getUser().getId(),
                        entity.getExpiryDate()
                ));
    }

    @Override
    public void deleteByToken(String token) {
        securityTokenRepository.findByToken(token)
                .ifPresent(securityTokenRepository::delete);
    }

    @Override
    public void deleteExpiredTokens(LocalDateTime now) {
        securityTokenRepository.findAll().stream()
                .filter(t -> t.getExpiryDate().isBefore(now))
                .forEach(securityTokenRepository::delete);
    }
}
