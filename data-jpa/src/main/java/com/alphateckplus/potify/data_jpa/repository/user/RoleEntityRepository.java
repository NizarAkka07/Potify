package com.alphateckplus.potify.data_jpa.repository.user;

import com.alphateckplus.potify.data_jpa.entity.user.RoleEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA pour les roles.
 */
public interface RoleEntityRepository extends JpaRepository<RoleEntity, String> {
    Optional<RoleEntity> findByName(String name);
}
