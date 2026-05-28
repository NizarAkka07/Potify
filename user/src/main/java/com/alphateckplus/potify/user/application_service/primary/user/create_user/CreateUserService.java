package com.alphateckplus.potify.user.application_service.primary.user.create_user;

import com.alphateckplus.potify.user.domain.model.User;

/**
 * Port d'entree pour creer un utilisateur.
 */
public interface CreateUserService {

    /**
     * Cree un nouvel utilisateur selon les regles metier.
     */
    User execute(User user);
}
