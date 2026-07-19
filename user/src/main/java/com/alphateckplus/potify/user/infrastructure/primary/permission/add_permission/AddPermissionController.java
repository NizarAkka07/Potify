package com.alphateckplus.potify.user.infrastructure.primary.permission.add_permission;

import com.alphateckplus.potify.user.application_service.primary.permission.create_permission.CreatePermissionService;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.CreatePermissionRequest;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.PermissionResponse;
import com.alphateckplus.potify.user.infrastructure.primary.permission.mapper.PermissionRestMapper;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de creation d'une permission.
 */
@RestController
@RequestMapping("/api/v1/access/permissions")
@RequiredArgsConstructor
public class AddPermissionController {

    private final CreatePermissionService createPermissionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_DASHBOARD') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<PermissionResponse> createPermission(
        @Valid @RequestBody CreatePermissionRequest request
    ) {
        PermissionRestMapper mapper = new PermissionRestMapper();
        var createdPermission = createPermissionService.execute(mapper.toDomain(request));

        return ResponseEntity
            .created(URI.create("/api/v1/access/permissions/" + createdPermission.getId()))
            .body(mapper.toResponse(createdPermission));
    }
}
