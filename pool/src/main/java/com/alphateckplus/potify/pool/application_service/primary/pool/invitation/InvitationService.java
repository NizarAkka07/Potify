package com.alphateckplus.potify.pool.application_service.primary.pool.invitation;

import com.alphateckplus.potify.pool.domain.model.Invitation;
import java.util.List;

public interface InvitationService {
    Invitation inviteUser(String poolId, String email);
    List<Invitation> getPoolInvitations(String poolId);
    boolean isUserInvited(String poolId, String email);
    Invitation acceptInvitation(String token);
    void deleteInvitation(String invitationId);
}
