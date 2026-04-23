package com.alphateckplus.potify.user.application_service.primary.create_user;

import com.alphateckplus.potify.user.application_service.primary.command.CreateUserCommand;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.UserAlreadyExistsException;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.UserStatus;

/**
 * Implementation par defaut du cas d'usage de creation utilisateur.
 */
public class DefaultCreateUserService implements CreateUserService {

    private final UserRepositoryPort userRepositoryPort;

    public DefaultCreateUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User execute(CreateUserCommand command) {
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        User userToCreate = User.builder()
            .fullName(command.fullName())
            .email(command.email())
            .password(command.password())
            .status(UserStatus.ACTIVE)
            .build();

        return userRepositoryPort.save(userToCreate);
    }
}
