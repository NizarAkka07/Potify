package com.alphateckplus.potify.user.infrastructure.primary.role.remove_permission_from_role;

import com.alphateckplus.potify.user.application_service.primary.role.remove_permission_from_role.RemovePermissionFromRoleService;
import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.domain.model.Permission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/access/roles")
public class RemovePermissionFromRoleController {

    private final RemovePermissionFromRoleService removePermissionFromRoleService;

    public RemovePermissionFromRoleController(RemovePermissionFromRoleService removePermissionFromRoleService) {
        this.removePermissionFromRoleService = removePermissionFromRoleService;
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermissionFromRole(
        @PathVariable String roleId,
        @PathVariable String permissionId
    ) {
        removePermissionFromRoleService.execute(
            Role.builder().id(roleId).build(),
            Permission.builder().id(permissionId).build()
        );
        return ResponseEntity.noContent().build();
    }
}
