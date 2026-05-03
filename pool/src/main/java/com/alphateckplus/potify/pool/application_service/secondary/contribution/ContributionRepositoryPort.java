package com.alphateckplus.potify.pool.application_service.secondary.contribution;

import com.alphateckplus.potify.pool.domain.model.Contribution;
import java.util.List;
import java.util.Optional;

/**
 * Port secondaire pour la persistence des contributions.
 */
public interface ContributionRepositoryPort {

    Contribution save(Contribution contribution);

    Optional<Contribution> findById(String id);

    List<Contribution> findByPoolId(String poolId);

    List<Contribution> findByUserId(String userId);
}
