package com.alphateckplus.potify.user.application_service.secondary.permission;

import com.alphateckplus.potify.user.domain.model.Permission;
import java.util.List;
import java.util.Optional;

/**
 * Port de sortie pour la persistence des permissions.
 */
public interface PermissionRepositoryPort {

    Permission save(Permission permission);

    Optional<Permission> findById(String id);

    Optional<Permission> findByCode(String code);

    List<Permission> findAll();

    void deleteById(String permissionId);
}
