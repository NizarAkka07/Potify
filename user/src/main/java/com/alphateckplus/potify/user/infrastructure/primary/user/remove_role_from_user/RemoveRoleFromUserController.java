package com.alphateckplus.potify.user.infrastructure.primary.user.remove_role_from_user;

import com.alphateckplus.potify.user.application_service.primary.command.RemoveRoleFromUserCommand;
import com.alphateckplus.potify.user.application_service.primary.user.remove_role_from_user.RemoveRoleFromUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/access/users")
public class RemoveRoleFromUserController {

    private final RemoveRoleFromUserService removeRoleFromUserService;

    public RemoveRoleFromUserController(RemoveRoleFromUserService removeRoleFromUserService) {
        this.removeRoleFromUserService = removeRoleFromUserService;
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRoleFromUser(
        @PathVariable String userId,
        @PathVariable String roleId
    ) {
        removeRoleFromUserService.execute(new RemoveRoleFromUserCommand(userId, roleId));
        return ResponseEntity.noContent().build();
    }
}
