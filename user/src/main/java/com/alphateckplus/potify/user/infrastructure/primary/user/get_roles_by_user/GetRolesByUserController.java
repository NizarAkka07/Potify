package com.alphateckplus.potify.user.infrastructure.primary.user.get_roles_by_user;

import com.alphateckplus.potify.user.application_service.primary.user.list_roles_by_user.ListRolesByUserService;
import com.alphateckplus.potify.user.infrastructure.primary.role.dto.RoleResponse;
import com.alphateckplus.potify.user.infrastructure.primary.permission.dto.PermissionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de lecture des roles d'un user.
 */
@RestController
@RequestMapping("/api/v1/access/users")
@RequiredArgsConstructor
public class GetRolesByUserController {

    private final ListRolesByUserService listRolesByUserService;

    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<RoleResponse>> listRolesByUser(@PathVariable String userId) {
        List<RoleResponse> body = listRolesByUserService.execute(userId)
            .stream()
            .map(role -> {
                List<PermissionResponse> perms = role.getPermissions() != null
                    ? role.getPermissions().stream()
                        .map(p -> new PermissionResponse(p.getId(), p.getCode(), p.getDescription()))
                        .toList()
                    : List.of();
                return new RoleResponse(role.getId(), role.getName(), role.getDescription(), perms);
            })
            .toList();

        return ResponseEntity.ok(body);
    }
}
