package com.alphateckplus.potify.user.application_service.primary.user.remove_role_from_user;

import com.alphateckplus.potify.user.application_service.primary.command.RemoveRoleFromUserCommand;

public interface RemoveRoleFromUserService {
    void execute(RemoveRoleFromUserCommand command);
}
