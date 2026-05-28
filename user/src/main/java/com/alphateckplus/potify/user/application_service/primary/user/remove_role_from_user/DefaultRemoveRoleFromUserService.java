package com.alphateckplus.potify.user.application_service.primary.user.remove_role_from_user;

import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.RoleNotFoundException;
import com.alphateckplus.potify.user.domain.exception.UserNotFoundException;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.Role;

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
    public void execute(User user, Role role) {
        userRepositoryPort.findById(user.getId())
            .orElseThrow(() -> new UserNotFoundException(user.getId()));

        roleRepositoryPort.findById(role.getId())
            .orElseThrow(() -> new RoleNotFoundException(role.getId()));

        userRepositoryPort.removeRoleFromUser(user.getId(), role.getId());
    }
}
