package com.alphateckplus.potify.user.infrastructure.primary.role.get_all_roles;

import com.alphateckplus.potify.user.application_service.primary.role.list_roles.ListRolesService;
import com.alphateckplus.potify.user.infrastructure.primary.role.dto.RoleResponse;
import com.alphateckplus.potify.user.infrastructure.primary.role.mapper.RoleRestMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de listing des roles.
 */
@RestController
@RequestMapping("/api/v1/access/roles")
@RequiredArgsConstructor
public class GetAllRolesController {

    private final ListRolesService listRolesService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> listRoles() {
        RoleRestMapper mapper = new RoleRestMapper();
        List<RoleResponse> body = listRolesService.execute()
            .stream()
            .map(mapper::toResponse)
            .toList();

        return ResponseEntity.ok(body);
    }
}
