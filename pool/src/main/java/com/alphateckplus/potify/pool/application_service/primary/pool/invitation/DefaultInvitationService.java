package com.alphateckplus.potify.pool.application_service.primary.pool.invitation;

import com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.InvitationRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.UserCheckPort;
import com.alphateckplus.potify.pool.domain.exception.UserNotFoundException;
import com.alphateckplus.potify.pool.domain.model.Invitation;
import com.alphateckplus.potify.pool.domain.model.Pool;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultInvitationService implements InvitationService {

    private final InvitationRepositoryPort invitationRepositoryPort;
    private final UserCheckPort userCheckPort;
    private final NotificationPort notificationPort;
    private final PoolRepositoryPort poolRepositoryPort;

    @Override
    public Invitation inviteUser(String poolId, String email) {
        // 1. Charger la cagnotte
        Pool pool = poolRepositoryPort.findById(poolId)
                .orElseThrow(() -> new RuntimeException("Cagnotte introuvable"));

        // 2. Verifier si l'utilisateur invite est le proprietaire
        String ownerEmail = userCheckPort.getEmailById(pool.getOwnerId());
        if (email.equalsIgnoreCase(ownerEmail)) {
            throw new RuntimeException("Vous ne pouvez pas vous inviter vous-même à votre propre cagnotte.");
        }

        // 3. Verifier si l'utilisateur existe dans le systeme
        if (!userCheckPort.existsByEmail(email)) {
            throw new UserNotFoundException(email);
        }

        // 4. Verifier si deja invite
        return invitationRepositoryPort.findByPoolIdAndEmail(poolId, email)
                .orElseGet(() -> {
                    Invitation invitation = Invitation.builder()
                            .poolId(poolId)
                            .email(email)
                            .status("PENDING")
                            .token(UUID.randomUUID().toString())
                            .build();
                    Invitation saved = invitationRepositoryPort.save(invitation);
                    
                    // 5. Envoyer l'email d'invitation
                    notificationPort.sendInvitationEmail(email, pool.getTitle(), saved.getToken());
                    
                    return saved;
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

    @Override
    public Invitation acceptInvitation(String token) {
        Invitation invitation = invitationRepositoryPort.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation invalide ou expirée"));
        
        invitation.setStatus("ACCEPTED");
        return invitationRepositoryPort.save(invitation);
    }

    @Override
    public void deleteInvitation(String invitationId) {
        invitationRepositoryPort.deleteById(invitationId);
    }
}
