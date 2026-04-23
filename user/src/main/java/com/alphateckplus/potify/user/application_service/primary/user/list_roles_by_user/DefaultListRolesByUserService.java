package com.alphateckplus.potify.user.application_service.primary.user.list_roles_by_user;

import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.UserNotFoundException;
import com.alphateckplus.potify.user.domain.model.Role;
import java.util.List;

/**
 * Implementation par defaut du cas d'usage de listing des roles d'un user.
 */
public class DefaultListRolesByUserService implements ListRolesByUserService {

    private final UserRepositoryPort userRepositoryPort;

    public DefaultListRolesByUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public List<Role> execute(String userId) {
        userRepositoryPort.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        return userRepositoryPort.findRolesByUserId(userId);
    }
}
