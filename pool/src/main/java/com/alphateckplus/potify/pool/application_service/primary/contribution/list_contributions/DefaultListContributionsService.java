package com.alphateckplus.potify.pool.application_service.primary.contribution.list_contributions;

import com.alphateckplus.potify.pool.application_service.secondary.contribution.ContributionRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Contribution;
import java.util.List;

/**
 * Implementation par defaut du service de liste des contributions.
 */
public class DefaultListContributionsService implements ListContributionsService {

    private final ContributionRepositoryPort contributionRepositoryPort;
    private final com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort poolRepositoryPort;

    public DefaultListContributionsService(
            ContributionRepositoryPort contributionRepositoryPort,
            com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort poolRepositoryPort) {
        this.contributionRepositoryPort = contributionRepositoryPort;
        this.poolRepositoryPort = poolRepositoryPort;
    }

    @Override
    public List<Contribution> findByUserId(String userId) {
        return contributionRepositoryPort.findByUserId(userId);
    }

    @Override
    public List<Contribution> findByPoolId(String poolId) {
        java.util.List<Contribution> contributions = new java.util.ArrayList<>(contributionRepositoryPort.findByPoolId(poolId));
        
        // Find if this pool has sub-pools (children)
        poolRepositoryPort.findById(poolId).ifPresent(pool -> {
            if (pool.getChildren() != null && !pool.getChildren().isEmpty()) {
                for (com.alphateckplus.potify.pool.domain.model.Pool child : pool.getChildren()) {
                    contributions.addAll(contributionRepositoryPort.findByPoolId(child.getId()));
                }
            }
        });

        // Sort by createdAt descending
        contributions.sort((c1, c2) -> {
            if (c1.getCreatedAt() == null && c2.getCreatedAt() == null) return 0;
            if (c1.getCreatedAt() == null) return 1;
            if (c2.getCreatedAt() == null) return -1;
            return c2.getCreatedAt().compareTo(c1.getCreatedAt());
        });

        return contributions;
    }
}
