package com.alphateckplus.potify.user.infrastructure.primary.role.delete_role;

import com.alphateckplus.potify.user.application_service.primary.role.delete_role.DeleteRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de suppression d'un role.
 */
@RestController
@RequestMapping("/api/v1/access/roles")
@RequiredArgsConstructor
public class DeleteRoleController {

    private final DeleteRoleService deleteRoleService;

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleId) {
        deleteRoleService.execute(roleId);
        return ResponseEntity.noContent().build();
    }
}
