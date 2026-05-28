package com.alphateckplus.potify.user.infrastructure.primary.role.add_role;

import com.alphateckplus.potify.user.application_service.primary.role.create_role.CreateRoleService;
import com.alphateckplus.potify.user.infrastructure.primary.role.dto.CreateRoleRequest;
import com.alphateckplus.potify.user.infrastructure.primary.role.dto.RoleResponse;
import com.alphateckplus.potify.user.infrastructure.primary.role.mapper.RoleRestMapper;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de creation d'un role.
 */
@RestController
@RequestMapping("/api/v1/access/roles")
@RequiredArgsConstructor
public class AddRoleController {

    private final CreateRoleService createRoleService;

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleRestMapper mapper = new RoleRestMapper();
        var createdRole = createRoleService.execute(mapper.toDomain(request));

        return ResponseEntity
            .created(URI.create("/api/v1/access/roles/" + createdRole.getId()))
            .body(mapper.toResponse(createdRole));
    }
}
