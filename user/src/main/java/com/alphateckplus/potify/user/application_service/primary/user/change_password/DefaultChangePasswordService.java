package com.alphateckplus.potify.user.application_service.primary.user.change_password;

import com.alphateckplus.potify.user.application_service.primary.command.ChangePasswordCommand;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.UserNotFoundException;
import com.alphateckplus.potify.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Implementation du changement de mot de passe avec verification et hashing.
 * Respecte SOLID en deleguant le hashing a une abstraction (PasswordEncoder).
 */
@RequiredArgsConstructor
public class DefaultChangePasswordService implements ChangePasswordService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void execute(ChangePasswordCommand command) {
        // 1. Recuperation de l'utilisateur
        User user = userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        // 2. Verification de l'ancien mot de passe
        if (!passwordEncoder.matches(command.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect");
        }

        // 3. Hashing du nouveau mot de passe et sauvegarde
        user.setPassword(passwordEncoder.encode(command.newPassword()));
        userRepositoryPort.save(user);
    }
}
