package com.alphateckplus.potify.user.application_service.primary.user.list_users;

import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.model.User;
import java.util.List;

/**
 * Implementation par defaut du cas d'usage de listing utilisateur.
 */
public class DefaultListUsersService implements ListUsersService {

    private final UserRepositoryPort userRepositoryPort;

    public DefaultListUsersService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public List<User> execute() {
        return userRepositoryPort.findAll();
    }
}
