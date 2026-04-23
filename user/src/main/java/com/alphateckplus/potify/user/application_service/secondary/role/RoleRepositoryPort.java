package com.alphateckplus.potify.user.application_service.secondary.role;

import com.alphateckplus.potify.user.domain.model.Role;
import java.util.List;
import java.util.Optional;

/**
 * Port de sortie pour la persistence des roles.
 */
public interface RoleRepositoryPort {

    Role save(Role role);

    Optional<Role> findById(String id);

    Optional<Role> findByName(String name);

    List<Role> findAll();

    void deleteById(String roleId);

    void addPermissionToRole(String roleId, String permissionId);
}
