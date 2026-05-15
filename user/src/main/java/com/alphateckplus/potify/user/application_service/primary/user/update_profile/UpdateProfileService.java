package com.alphateckplus.potify.user.application_service.primary.user.update_profile;

import com.alphateckplus.potify.user.application_service.primary.command.UpdateProfileCommand;
import com.alphateckplus.potify.user.domain.model.User;

/**
 * Port primaire definissant le contrat pour la mise a jour du profil utilisateur.
 * Suit le principe d'Inversion de Dependance (DIP) de SOLID.
 */
public interface UpdateProfileService {
    /**
     * Execute la mise a jour du profil.
     * @param command Les donnees de mise a jour
     * @return L'utilisateur mis a jour
     */
    User execute(UpdateProfileCommand command);
}
