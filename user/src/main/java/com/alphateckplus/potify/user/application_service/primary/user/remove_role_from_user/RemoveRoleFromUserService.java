package com.alphateckplus.potify.user.application_service.primary.user.remove_role_from_user;

import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.Role;

public interface RemoveRoleFromUserService {
    void execute(User user, Role role);
}
