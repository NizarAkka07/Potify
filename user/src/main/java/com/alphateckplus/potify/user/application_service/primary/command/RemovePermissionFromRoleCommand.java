package com.alphateckplus.potify.user.application_service.primary.command;

public record RemovePermissionFromRoleCommand(String roleId, String permissionId) {
}
