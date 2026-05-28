package com.alphateckplus.potify.user.application_service.primary.user.update_user;

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
    public User execute(User user) {
        User existingUser = userRepositoryPort.findById(user.getId())
            .orElseThrow(() -> new UserNotFoundException(user.getId()));

        if (!existingUser.getEmail().equals(user.getEmail())
            && userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail());
        }

        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        existingUser.setStatus(user.getStatus());
        return userRepositoryPort.save(existingUser);
    }
}
