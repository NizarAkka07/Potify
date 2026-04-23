package com.alphateckplus.potify.user.application_service.primary.user.update_user;

import com.alphateckplus.potify.user.application_service.primary.command.UpdateUserCommand;
import com.alphateckplus.potify.user.domain.model.User;

/**
 * Port d'entree pour modifier les informations d'un utilisateur.
 */
public interface UpdateUserService {

    /**
     * Met a jour le profil utilisateur.
     */
    User execute(UpdateUserCommand command);
}
