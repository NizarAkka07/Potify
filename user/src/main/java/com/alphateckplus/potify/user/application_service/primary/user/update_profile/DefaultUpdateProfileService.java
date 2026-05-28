package com.alphateckplus.potify.user.application_service.primary.user.update_profile;

import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
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
    public User execute(User user) {
        // 1. Verification de l'existence de l'utilisateur
        User existingUser = userRepositoryPort.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));

        // 2. Mise a jour uniquement du nom complet
        existingUser.setFullName(user.getFullName());
        
        // 3. Sauvegarde
        return userRepositoryPort.save(existingUser);
    }
}
