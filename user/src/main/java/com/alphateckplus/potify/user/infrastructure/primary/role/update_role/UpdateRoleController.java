package com.alphateckplus.potify.user.infrastructure.primary.role.update_role;

import com.alphateckplus.potify.user.application_service.primary.role.update_role.UpdateRoleService;
import com.alphateckplus.potify.user.infrastructure.primary.role.dto.RoleResponse;
import com.alphateckplus.potify.user.infrastructure.primary.role.dto.UpdateRoleRequest;
import com.alphateckplus.potify.user.infrastructure.primary.role.mapper.RoleRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de mise a jour d'un role.
 */
@RestController
@RequestMapping("/api/v1/access/roles")
@RequiredArgsConstructor
public class UpdateRoleController {

    private final UpdateRoleService updateRoleService;

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponse> updateRole(
        @PathVariable String roleId,
        @Valid @RequestBody UpdateRoleRequest request
    ) {
        RoleRestMapper mapper = new RoleRestMapper();
        var updatedRole = updateRoleService.execute(mapper.toUpdateCommand(roleId, request));
        return ResponseEntity.ok(mapper.toResponse(updatedRole));
    }
}
