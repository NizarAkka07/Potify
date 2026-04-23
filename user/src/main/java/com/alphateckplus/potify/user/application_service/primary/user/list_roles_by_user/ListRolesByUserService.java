package com.alphateckplus.potify.user.application_service.primary.user.list_roles_by_user;

import com.alphateckplus.potify.user.domain.model.Role;
import java.util.List;

/**
 * Port d'entree pour lister les roles d'un utilisateur.
 */
public interface ListRolesByUserService {

    List<Role> execute(String userId);
}
