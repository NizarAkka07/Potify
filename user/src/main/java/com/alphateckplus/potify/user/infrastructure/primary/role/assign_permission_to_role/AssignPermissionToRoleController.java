package com.alphateckplus.potify.user.infrastructure.primary.role.assign_permission_to_role;

import com.alphateckplus.potify.user.application_service.primary.role.assign_permission_to_role.AssignPermissionToRoleService;
import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.domain.model.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint d'affectation permission -> role.
 */
@RestController
@RequestMapping("/api/v1/access/roles")
@RequiredArgsConstructor
public class AssignPermissionToRoleController {

    private final AssignPermissionToRoleService assignPermissionToRoleService;

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> assignPermissionToRole(
        @PathVariable String roleId,
        @PathVariable String permissionId
    ) {
        assignPermissionToRoleService.execute(
            Role.builder().id(roleId).build(),
            Permission.builder().id(permissionId).build()
        );
        return ResponseEntity.noContent().build();
    }
}
