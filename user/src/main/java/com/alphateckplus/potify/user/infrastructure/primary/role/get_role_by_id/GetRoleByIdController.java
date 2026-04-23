package com.alphateckplus.potify.user.infrastructure.primary.role.get_role_by_id;

import com.alphateckplus.potify.user.application_service.primary.role.get_role_by_id.GetRoleByIdService;
import com.alphateckplus.potify.user.infrastructure.primary.role.dto.RoleResponse;
import com.alphateckplus.potify.user.infrastructure.primary.role.mapper.RoleRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de lecture d'un role par id.
 */
@RestController
@RequestMapping("/api/v1/access/roles")
@RequiredArgsConstructor
public class GetRoleByIdController {

    private final GetRoleByIdService getRoleByIdService;

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable String roleId) {
        RoleRestMapper mapper = new RoleRestMapper();
        return ResponseEntity.ok(mapper.toResponse(getRoleByIdService.execute(roleId)));
    }
}
