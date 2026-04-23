package com.alphateckplus.potify.user.application_service.primary.role.delete_role;

import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.RoleNotFoundException;

/**
 * Implementation par defaut du cas d'usage de suppression role.
 */
public class DefaultDeleteRoleService implements DeleteRoleService {

    private final RoleRepositoryPort roleRepositoryPort;

    public DefaultDeleteRoleService(RoleRepositoryPort roleRepositoryPort) {
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public void execute(String roleId) {
        roleRepositoryPort.findById(roleId)
            .orElseThrow(() -> new RoleNotFoundException(roleId));

        roleRepositoryPort.deleteById(roleId);
    }
}
