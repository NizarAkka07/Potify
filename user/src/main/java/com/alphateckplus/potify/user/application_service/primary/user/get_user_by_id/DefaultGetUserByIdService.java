package com.alphateckplus.potify.user.application_service.primary.user.get_user_by_id;

import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.UserNotFoundException;
import com.alphateckplus.potify.user.domain.model.User;

/**
 * Implementation par defaut du cas d'usage de lecture utilisateur par id.
 */
public class DefaultGetUserByIdService implements GetUserByIdService {

    private final UserRepositoryPort userRepositoryPort;

    public DefaultGetUserByIdService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User execute(String userId) {
        return userRepositoryPort.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
