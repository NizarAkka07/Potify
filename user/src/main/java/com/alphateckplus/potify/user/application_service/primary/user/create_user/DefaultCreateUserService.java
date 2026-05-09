package com.alphateckplus.potify.user.application_service.primary.user.create_user;

import com.alphateckplus.potify.user.application_service.primary.command.CreateUserCommand;
import com.alphateckplus.potify.user.application_service.secondary.notification.NotificationPort;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.UserAlreadyExistsException;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.UserStatus;

/**
 * Implementation par defaut du cas d'usage de creation utilisateur.
 */
public class DefaultCreateUserService implements CreateUserService {

    private final UserRepositoryPort userRepositoryPort;
    private final NotificationPort notificationPort;

    public DefaultCreateUserService(UserRepositoryPort userRepositoryPort, NotificationPort notificationPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public User execute(CreateUserCommand command) {
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        String token = java.util.UUID.randomUUID().toString();
        User userToCreate = User.builder()
            .fullName(command.fullName())
            .email(command.email())
            .password(command.password())
            .status(UserStatus.PENDING_VERIFICATION)
            .enabled(false)
            .verificationToken(token)
            .build();

        User createdUser = userRepositoryPort.save(userToCreate);

        // Envoi du mail de verification
        notificationPort.sendVerificationEmail(createdUser.getEmail(), createdUser.getFullName(), token);

        return createdUser;
    }
}
