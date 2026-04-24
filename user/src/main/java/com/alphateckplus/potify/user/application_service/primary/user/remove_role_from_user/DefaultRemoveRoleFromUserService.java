package com.alphateckplus.potify.user.application_service.primary.user.remove_role_from_user;

import com.alphateckplus.potify.user.application_service.primary.command.RemoveRoleFromUserCommand;
import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.RoleNotFoundException;
import com.alphateckplus.potify.user.domain.exception.UserNotFoundException;

public class DefaultRemoveRoleFromUserService implements RemoveRoleFromUserService {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;

    public DefaultRemoveRoleFromUserService(
        UserRepositoryPort userRepositoryPort,
        RoleRepositoryPort roleRepositoryPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public void execute(RemoveRoleFromUserCommand command) {
        userRepositoryPort.findById(command.userId())
            .orElseThrow(() -> new UserNotFoundException(command.userId()));

        roleRepositoryPort.findById(command.roleId())
            .orElseThrow(() -> new RoleNotFoundException(command.roleId()));

        userRepositoryPort.removeRoleFromUser(command.userId(), command.roleId());
    }
}
