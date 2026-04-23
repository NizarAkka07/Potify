package com.alphateckplus.potify.user.application_service.primary.user.assign_role_to_user;

import com.alphateckplus.potify.user.application_service.primary.command.AssignRoleToUserCommand;
import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.RoleNotFoundException;
import com.alphateckplus.potify.user.domain.exception.UserNotFoundException;

/**
 * Implementation par defaut du cas d'usage d'affectation role -> utilisateur.
 */
public class DefaultAssignRoleToUserService implements AssignRoleToUserService {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;

    public DefaultAssignRoleToUserService(
        UserRepositoryPort userRepositoryPort,
        RoleRepositoryPort roleRepositoryPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public void execute(AssignRoleToUserCommand command) {
        userRepositoryPort.findById(command.userId())
            .orElseThrow(() -> new UserNotFoundException(command.userId()));

        roleRepositoryPort.findById(command.roleId())
            .orElseThrow(() -> new RoleNotFoundException(command.roleId()));

        userRepositoryPort.addRoleToUser(command.userId(), command.roleId());
    }
}
