package com.alphateckplus.potify.user.application_service.primary.role.remove_permission_from_role;

import com.alphateckplus.potify.user.application_service.primary.command.RemovePermissionFromRoleCommand;

public interface RemovePermissionFromRoleService {
    void execute(RemovePermissionFromRoleCommand command);
}
