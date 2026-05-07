package com.alphateckplus.potify.pool.application_service.primary.contribution.list_contributions;

import com.alphateckplus.potify.pool.application_service.secondary.contribution.ContributionRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Contribution;
import java.util.List;

/**
 * Implementation par defaut du service de liste des contributions.
 */
public class DefaultListContributionsService implements ListContributionsService {

    private final ContributionRepositoryPort contributionRepositoryPort;

    public DefaultListContributionsService(ContributionRepositoryPort contributionRepositoryPort) {
        this.contributionRepositoryPort = contributionRepositoryPort;
    }

    @Override
    public List<Contribution> findByUserId(String userId) {
        return contributionRepositoryPort.findByUserId(userId);
    }

    @Override
    public List<Contribution> findByPoolId(String poolId) {
        return contributionRepositoryPort.findByPoolId(poolId);
    }
}
