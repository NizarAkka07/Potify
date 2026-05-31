package com.alphateckplus.potify.pool.infrastructure.primary.pool.invitation.delete_invitation;

import com.alphateckplus.potify.pool.application_service.primary.pool.invitation.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "Pool Invitations")
public class DeleteInvitationController {

    private final InvitationService invitationService;

    @DeleteMapping("/{invitationId}")
    @Operation(summary = "Supprimer une invitation")
    public ResponseEntity<Void> deleteInvitation(@PathVariable String invitationId) {
        invitationService.deleteInvitation(invitationId);
        return ResponseEntity.noContent().build();
    }
}
