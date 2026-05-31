package com.alphateckplus.potify.pool.infrastructure.primary.pool.invitation.invite_user;

import com.alphateckplus.potify.pool.application_service.primary.pool.invitation.InvitationService;
import com.alphateckplus.potify.pool.domain.model.Invitation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "Pool Invitations")
public class InviteUserController {

    private final InvitationService invitationService;

    @PostMapping("/pool/{poolId}")
    @Operation(summary = "Inviter un utilisateur par email")
    public ResponseEntity<Invitation> inviteUser(
            @PathVariable String poolId,
            @RequestParam String email) {
        return ResponseEntity.ok(invitationService.inviteUser(poolId, email));
    }
}
