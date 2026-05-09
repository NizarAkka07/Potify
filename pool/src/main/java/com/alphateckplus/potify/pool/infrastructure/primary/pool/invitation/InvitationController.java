package com.alphateckplus.potify.pool.infrastructure.primary.pool.invitation;

import com.alphateckplus.potify.pool.application_service.primary.pool.invitation.InvitationService;
import com.alphateckplus.potify.pool.domain.model.Invitation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "Pool Invitations", description = "Gestion des invitations pour les tontines privees")
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/pool/{poolId}")
    @Operation(summary = "Inviter un utilisateur par email")
    public ResponseEntity<Invitation> inviteUser(
            @PathVariable String poolId,
            @RequestParam String email) {
        return ResponseEntity.ok(invitationService.inviteUser(poolId, email));
    }

    @GetMapping("/pool/{poolId}")
    @Operation(summary = "Lister les invitations d'une cagnotte")
    public ResponseEntity<List<Invitation>> getPoolInvitations(@PathVariable String poolId) {
        return ResponseEntity.ok(invitationService.getPoolInvitations(poolId));
    }

    @PostMapping("/accept")
    @Operation(summary = "Accepter une invitation via token")
    public ResponseEntity<Invitation> acceptInvitation(@RequestParam String token) {
        return ResponseEntity.ok(invitationService.acceptInvitation(token));
    }
}
