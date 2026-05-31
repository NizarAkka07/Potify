package com.alphateckplus.potify.pool.infrastructure.primary.pool.invitation.accept_invitation;

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
public class AcceptInvitationController {

    private final InvitationService invitationService;

    @PostMapping("/accept")
    @Operation(summary = "Accepter une invitation via token")
    public ResponseEntity<Invitation> acceptInvitation(@RequestParam String token) {
        return ResponseEntity.ok(invitationService.acceptInvitation(token));
    }
}
