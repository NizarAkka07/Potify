package com.alphateckplus.potify.user.application_service.primary.role.get_role_by_id;

import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.RoleNotFoundException;
import com.alphateckplus.potify.user.domain.model.Role;

/**
 * Implementation par defaut du cas d'usage de lecture role par id.
 */
public class DefaultGetRoleByIdService implements GetRoleByIdService {

    private final RoleRepositoryPort roleRepositoryPort;

    public DefaultGetRoleByIdService(RoleRepositoryPort roleRepositoryPort) {
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public Role execute(String roleId) {
        return roleRepositoryPort.findById(roleId)
            .orElseThrow(() -> new RoleNotFoundException(roleId));
    }
}
