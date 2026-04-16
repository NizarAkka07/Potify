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

    boolean existsByEmail(String email);
}
