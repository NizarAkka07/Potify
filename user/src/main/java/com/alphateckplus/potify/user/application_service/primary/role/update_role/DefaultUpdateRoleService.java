package com.alphateckplus.potify.user.application_service.primary.role.update_role;

import com.alphateckplus.potify.user.application_service.primary.command.UpdateRoleCommand;
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
    public Role execute(UpdateRoleCommand command) {
        Role existingRole = roleRepositoryPort.findById(command.roleId())
            .orElseThrow(() -> new RoleNotFoundException(command.roleId()));

        roleRepositoryPort.findByName(command.name())
            .ifPresent(role -> {
                if (!role.getId().equals(existingRole.getId())) {
                    throw new RoleAlreadyExistsException(command.name());
                }
            });

        existingRole.setName(command.name());
        existingRole.setDescription(command.description());
        return roleRepositoryPort.save(existingRole);
    }
}
