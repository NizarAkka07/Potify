package com.alphateckplus.potify.pool.infrastructure.primary.pool.invitation.get_pool_invitations;

import com.alphateckplus.potify.pool.application_service.primary.pool.invitation.InvitationService;
import com.alphateckplus.potify.pool.domain.model.Invitation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "Pool Invitations")
public class GetPoolInvitationsController {

    private final InvitationService invitationService;

    @GetMapping("/pool/{poolId}")
    @Operation(summary = "Lister les invitations d'une cagnotte")
    public ResponseEntity<List<Invitation>> getPoolInvitations(@PathVariable String poolId) {
        return ResponseEntity.ok(invitationService.getPoolInvitations(poolId));
    }
}
