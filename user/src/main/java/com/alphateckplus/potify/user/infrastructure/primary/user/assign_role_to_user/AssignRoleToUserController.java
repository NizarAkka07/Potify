package com.alphateckplus.potify.user.infrastructure.primary.user.assign_role_to_user;

import com.alphateckplus.potify.user.application_service.primary.user.assign_role_to_user.AssignRoleToUserService;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint d'affectation role -> user.
 */
@RestController
@RequestMapping("/api/v1/access/users")
@RequiredArgsConstructor
public class AssignRoleToUserController {

    private final AssignRoleToUserService assignRoleToUserService;

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> assignRoleToUser(
        @PathVariable String userId,
        @PathVariable String roleId
    ) {
        assignRoleToUserService.execute(
            User.builder().id(userId).build(),
            Role.builder().id(roleId).build()
        );
        return ResponseEntity.noContent().build();
    }
}
