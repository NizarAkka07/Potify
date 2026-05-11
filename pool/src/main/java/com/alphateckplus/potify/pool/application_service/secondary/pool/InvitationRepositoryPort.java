package com.alphateckplus.potify.pool.application_service.secondary.pool;

import com.alphateckplus.potify.pool.domain.model.Invitation;
import java.util.List;
import java.util.Optional;

public interface InvitationRepositoryPort {
    Invitation save(Invitation invitation);
    List<Invitation> findByPoolId(String poolId);
    Optional<Invitation> findByPoolIdAndEmail(String poolId, String email);
    Optional<Invitation> findByToken(String token);
    void deleteById(String id);
}
