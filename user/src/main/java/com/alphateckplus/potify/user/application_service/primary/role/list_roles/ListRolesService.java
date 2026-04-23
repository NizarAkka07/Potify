package com.alphateckplus.potify.user.application_service.primary.role.list_roles;

import com.alphateckplus.potify.user.domain.model.Role;
import java.util.List;

/**
 * Port d'entree pour lister tous les roles.
 */
public interface ListRolesService {

    List<Role> execute();
}
