package com.alphateckplus.potify.pool.application_service.primary.pool.invitation;

import com.alphateckplus.potify.pool.application_service.secondary.pool.InvitationRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Invitation;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultInvitationService implements InvitationService {

    private final InvitationRepositoryPort invitationRepositoryPort;

    @Override
    public Invitation inviteUser(String poolId, String email) {
        // Verifier si deja invite
        return invitationRepositoryPort.findByPoolIdAndEmail(poolId, email)
                .orElseGet(() -> {
                    Invitation invitation = Invitation.builder()
                            .poolId(poolId)
                            .email(email)
                            .status("PENDING")
                            .token(UUID.randomUUID().toString())
                            .build();
                    return invitationRepositoryPort.save(invitation);
                });
    }

    @Override
    public List<Invitation> getPoolInvitations(String poolId) {
        return invitationRepositoryPort.findByPoolId(poolId);
    }

    @Override
    public boolean isUserInvited(String poolId, String email) {
        return invitationRepositoryPort.findByPoolIdAndEmail(poolId, email).isPresent();
    }
}
