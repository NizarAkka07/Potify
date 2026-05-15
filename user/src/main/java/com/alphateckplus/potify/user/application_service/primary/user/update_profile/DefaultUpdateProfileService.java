package com.alphateckplus.potify.user.application_service.primary.user.update_profile;

import com.alphateckplus.potify.user.application_service.primary.command.UpdateProfileCommand;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.UserAlreadyExistsException;
import com.alphateckplus.potify.user.domain.exception.UserNotFoundException;
import com.alphateckplus.potify.user.domain.model.User;
import lombok.RequiredArgsConstructor;

/**
 * Implementation du cas d'usage de mise a jour du profil.
 * Centralise la logique metier et interagit avec les ports secondaires.
 */
@RequiredArgsConstructor
public class DefaultUpdateProfileService implements UpdateProfileService {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    public User execute(UpdateProfileCommand command) {
        // 1. Verification de l'existence de l'utilisateur
        User user = userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        // 2. Mise a jour uniquement du nom complet
        user.setFullName(command.fullName());
        
        // 3. Sauvegarde
        return userRepositoryPort.save(user);
    }
}
