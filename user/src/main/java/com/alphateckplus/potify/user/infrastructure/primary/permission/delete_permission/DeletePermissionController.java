package com.alphateckplus.potify.user.infrastructure.primary.permission.delete_permission;

import com.alphateckplus.potify.user.application_service.primary.permission.delete_permission.DeletePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de suppression d'une permission.
 */
@RestController
@RequestMapping("/api/v1/access/permissions")
@RequiredArgsConstructor
public class DeletePermissionController {

    private final DeletePermissionService deletePermissionService;

    @DeleteMapping("/{permissionId}")
    public ResponseEntity<Void> deletePermission(@PathVariable String permissionId) {
        deletePermissionService.execute(permissionId);
        return ResponseEntity.noContent().build();
    }
}
