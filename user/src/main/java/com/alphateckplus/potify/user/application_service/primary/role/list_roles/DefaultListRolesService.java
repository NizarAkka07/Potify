package com.alphateckplus.potify.user.application_service.primary.role.list_roles;

import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.domain.model.Role;
import java.util.List;

/**
 * Implementation par defaut du cas d'usage de listing des roles.
 */
public class DefaultListRolesService implements ListRolesService {

    private final RoleRepositoryPort roleRepositoryPort;

    public DefaultListRolesService(RoleRepositoryPort roleRepositoryPort) {
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public List<Role> execute() {
        return roleRepositoryPort.findAll();
    }
}
