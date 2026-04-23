package com.alphateckplus.potify.user.infrastructure.primary.permission.get_permission_by_id;

import com.alphateckplus.potify.user.application_service.primary.permission.get_permission_by_id.GetPermissionByIdService;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.PermissionResponse;
import com.alphateckplus.potify.user.infrastructure.primary.permission.mapper.PermissionRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de lecture d'une permission par id.
 */
@RestController
@RequestMapping("/api/v1/access/permissions")
@RequiredArgsConstructor
public class GetPermissionByIdController {

    private final GetPermissionByIdService getPermissionByIdService;

    @GetMapping("/{permissionId}")
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable String permissionId) {
        PermissionRestMapper mapper = new PermissionRestMapper();
        return ResponseEntity.ok(mapper.toResponse(getPermissionByIdService.execute(permissionId)));
    }
}
