package com.alphateckplus.potify.user.application_service.primary.user.update_user;

import com.alphateckplus.potify.user.application_service.primary.command.UpdateUserCommand;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.UserAlreadyExistsException;
import com.alphateckplus.potify.user.domain.exception.UserNotFoundException;
import com.alphateckplus.potify.user.domain.model.User;

/**
 * Implementation par defaut du cas d'usage de mise a jour utilisateur.
 */
public class DefaultUpdateUserService implements UpdateUserService {

    private final UserRepositoryPort userRepositoryPort;

    public DefaultUpdateUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User execute(UpdateUserCommand command) {
        User existingUser = userRepositoryPort.findById(command.userId())
            .orElseThrow(() -> new UserNotFoundException(command.userId()));

        if (!existingUser.getEmail().equals(command.email())
            && userRepositoryPort.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        existingUser.setFullName(command.fullName());
        existingUser.setEmail(command.email());
        existingUser.setPassword(command.password());
        existingUser.setStatus(command.status());
        return userRepositoryPort.save(existingUser);
    }
}
