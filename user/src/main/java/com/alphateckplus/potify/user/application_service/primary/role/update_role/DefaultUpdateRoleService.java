package com.alphateckplus.potify.user.application_service.primary.role.update_role;

import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.RoleAlreadyExistsException;
import com.alphateckplus.potify.user.domain.exception.RoleNotFoundException;
import com.alphateckplus.potify.user.domain.model.Role;

/**
 * Implementation par defaut du cas d'usage de mise a jour role.
 */
public class DefaultUpdateRoleService implements UpdateRoleService {

    private final RoleRepositoryPort roleRepositoryPort;

    public DefaultUpdateRoleService(RoleRepositoryPort roleRepositoryPort) {
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public Role execute(Role role) {
        Role existingRole = roleRepositoryPort.findById(role.getId())
            .orElseThrow(() -> new RoleNotFoundException(role.getId()));

        roleRepositoryPort.findByName(role.getName())
            .ifPresent(r -> {
                if (!r.getId().equals(existingRole.getId())) {
                    throw new RoleAlreadyExistsException(role.getName());
                }
            });

        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        return roleRepositoryPort.save(existingRole);
    }
}
