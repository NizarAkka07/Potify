package com.alphateckplus.potify.user.infrastructure.primary.permission.get_all_permissions;

import com.alphateckplus.potify.user.application_service.primary.permission.list_permissions.ListPermissionsService;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.PermissionResponse;
import com.alphateckplus.potify.user.infrastructure.primary.permission.mapper.PermissionRestMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de listing des permissions.
 */
@RestController
@RequestMapping("/api/v1/access/permissions")
@RequiredArgsConstructor
public class GetAllPermissionsController {

    private final ListPermissionsService listPermissionsService;

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> listPermissions() {
        PermissionRestMapper mapper = new PermissionRestMapper();
        List<PermissionResponse> body = listPermissionsService.execute()
            .stream()
            .map(mapper::toResponse)
            .toList();

        return ResponseEntity.ok(body);
    }
}
