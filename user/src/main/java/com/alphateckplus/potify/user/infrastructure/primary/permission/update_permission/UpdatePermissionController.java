package com.alphateckplus.potify.user.infrastructure.primary.permission.update_permission;

import com.alphateckplus.potify.user.application_service.primary.permission.update_permission.UpdatePermissionService;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.PermissionResponse;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.UpdatePermissionRequest;
import com.alphateckplus.potify.user.infrastructure.primary.permission.mapper.PermissionRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de mise a jour d'une permission.
 */
@RestController
@RequestMapping("/api/v1/access/permissions")
@RequiredArgsConstructor
public class UpdatePermissionController {

    private final UpdatePermissionService updatePermissionService;

    @PutMapping("/{permissionId}")
    public ResponseEntity<PermissionResponse> updatePermission(
        @PathVariable String permissionId,
        @Valid @RequestBody UpdatePermissionRequest request
    ) {
        PermissionRestMapper mapper = new PermissionRestMapper();
        var updatedPermission = updatePermissionService.execute(
            mapper.toDomain(permissionId, request)
        );

        return ResponseEntity.ok(mapper.toResponse(updatedPermission));
    }
}
