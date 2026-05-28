package com.alphateckplus.potify.user.application_service.primary.user.change_password;

import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.UserNotFoundException;
import com.alphateckplus.potify.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import com.alphateckplus.potify.user.application_service.secondary.user.PasswordHashingPort;

/**
 * Implementation du changement de mot de passe avec verification et hashing.
 * Respecte SOLID en deleguant le hashing a une abstraction (PasswordHashingPort).
 */
@RequiredArgsConstructor
public class DefaultChangePasswordService implements ChangePasswordService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHashingPort passwordHashingPort;

    @Override
    public void execute(User user, String oldPassword) {
        // 1. Recuperation de l'utilisateur
        User existingUser = userRepositoryPort.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));

        // 2. Verification de l'ancien mot de passe
        if (!passwordHashingPort.matches(oldPassword, existingUser.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect");
        }

        // 3. Hashing du nouveau mot de passe et sauvegarde
        existingUser.setPassword(passwordHashingPort.hash(user.getPassword()));
        userRepositoryPort.save(existingUser);
    }
}
