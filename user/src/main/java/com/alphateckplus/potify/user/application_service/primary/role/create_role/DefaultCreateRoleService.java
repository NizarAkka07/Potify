package com.alphateckplus.potify.user.application_service.primary.role.create_role;

import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.RoleAlreadyExistsException;
import com.alphateckplus.potify.user.domain.model.Role;

/**
 * Implementation par defaut du cas d'usage de creation role.
 */
public class DefaultCreateRoleService implements CreateRoleService {

    private final RoleRepositoryPort roleRepositoryPort;

    public DefaultCreateRoleService(RoleRepositoryPort roleRepositoryPort) {
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public Role execute(Role role) {
        if (roleRepositoryPort.findByName(role.getName()).isPresent()) {
            throw new RoleAlreadyExistsException(role.getName());
        }

        return roleRepositoryPort.save(role);
    }
}
