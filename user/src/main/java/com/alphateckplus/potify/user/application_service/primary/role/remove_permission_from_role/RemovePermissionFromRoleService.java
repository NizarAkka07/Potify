package com.alphateckplus.potify.user.application_service.primary.role.remove_permission_from_role;

import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.domain.model.Permission;

public interface RemovePermissionFromRoleService {
    void execute(Role role, Permission permission);
}
