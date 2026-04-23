package com.alphateckplus.potify.user.application_service.primary.user.get_user_by_id;

import com.alphateckplus.potify.user.domain.model.User;

/**
 * Port d'entree pour recuperer un utilisateur par son identifiant.
 */
public interface GetUserByIdService {

    /**
     * Recupere un utilisateur par id.
     */
    User execute(String userId);
}
