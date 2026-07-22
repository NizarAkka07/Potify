package com.alphateckplus.potify.user.infrastructure.secondary.user;

import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.UserStatus;

/**
 * Mapper dedie a la conversion Domain <-> JPA Entity.
 *
 * <p>Ce composant centralise la transformation et evite de la dupliquer.
 */
public class UserPersistenceMapper {

    /**
     * Convertit un objet domaine vers son equivalent de persistance.
     */
    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
            .id(domain.getId())
            .fullName(domain.getFullName())
            .email(domain.getEmail())
            .password(domain.getPassword())
            .status(domain.getStatus() != null ? com.alphateckplus.potify.data_jpa.entity.user.UserStatus.valueOf(domain.getStatus().name()) : com.alphateckplus.potify.data_jpa.entity.user.UserStatus.ACTIVE)
            .enabled(domain.isEnabled())
            .accountNonLocked(domain.isAccountNonLocked())
            .failedAttempts(domain.getFailedAttempts())
            .lockTime(domain.getLockTime())
            .verificationToken(domain.getVerificationToken())
            .avatarUrl(domain.getAvatarUrl())
            .adminVerification(domain.isAdminVerification())
            .build();
    }

    /**
     * Convertit une entite JPA vers l'objet metier du domaine.
     */
    public User toDomain(UserEntity entity) {
        return User.builder()
            .id(entity.getId())
            .fullName(entity.getFullName())
            .email(entity.getEmail())
            .password(entity.getPassword())
            .status(entity.getStatus() != null ? UserStatus.valueOf(entity.getStatus().name()) : UserStatus.ACTIVE)
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .enabled(entity.isEnabled())
            .accountNonLocked(entity.isAccountNonLocked())
            .failedAttempts(entity.getFailedAttempts())
            .lockTime(entity.getLockTime())
            .verificationToken(entity.getVerificationToken())
            .avatarUrl(entity.getAvatarUrl())
            .adminVerification(entity.isAdminVerification())
            .build();
    }
}
