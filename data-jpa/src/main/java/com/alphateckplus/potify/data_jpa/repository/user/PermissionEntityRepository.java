package com.alphateckplus.potify.data_jpa.repository.user;

import com.alphateckplus.potify.data_jpa.entity.user.PermissionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA pour les permissions.
 */
public interface PermissionEntityRepository extends JpaRepository<PermissionEntity, String> {
    Optional<PermissionEntity> findByCode(String code);
}
