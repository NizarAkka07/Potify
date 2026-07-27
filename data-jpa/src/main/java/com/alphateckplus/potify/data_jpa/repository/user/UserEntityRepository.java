package com.alphateckplus.potify.data_jpa.repository.user;

import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA pour UserEntity.
 *
 * <p>Spring genere automatiquement l'implementation a partir de l'interface,
 * ce qui reduit le code boilerplate et respecte le principe DIP (on depend
 * d'une abstraction repository, pas d'une implementation concrete).
 */
public interface UserEntityRepository extends JpaRepository<UserEntity, String> {

    /**
     * Exemple de requete derivee par convention de nommage Spring Data.
     */
    Optional<UserEntity> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<UserEntity> findWithRolesByEmail(@org.springframework.data.repository.query.Param("email") String email);

    Optional<UserEntity> findByVerificationToken(String verificationToken);

    boolean existsByEmail(String email);

    java.util.List<UserEntity> findByAdminVerification(boolean adminVerification);
}
