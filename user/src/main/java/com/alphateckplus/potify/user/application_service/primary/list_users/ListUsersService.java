package com.alphateckplus.potify.user.application_service.primary.list_users;

import com.alphateckplus.potify.user.domain.model.User;
import java.util.List;

/**
 * Port d'entree pour lister les utilisateurs.
 */
public interface ListUsersService {

    /**
     * Retourne tous les utilisateurs.
     */
    List<User> execute();
}
