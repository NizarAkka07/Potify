package com.alphateckplus.potify.pool.application_service.primary.contribution.create_contribution;

import com.alphateckplus.potify.pool.application_service.primary.command.CreateContributionCommand;
import com.alphateckplus.potify.pool.application_service.secondary.contribution.ContributionRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.domain.exception.PoolNotFoundException;
import com.alphateckplus.potify.pool.domain.model.Contribution;
import com.alphateckplus.potify.pool.domain.model.ContributionStatus;
import com.alphateckplus.potify.pool.domain.model.Pool;
import java.time.Instant;
import java.util.UUID;

/**
 * Implementation par defaut du service de creation de contribution.
 */
public class DefaultCreateContributionService implements CreateContributionService {

    private final ContributionRepositoryPort contributionRepositoryPort;
    private final PoolRepositoryPort poolRepositoryPort;

    public DefaultCreateContributionService(
            ContributionRepositoryPort contributionRepositoryPort,
            PoolRepositoryPort poolRepositoryPort) {
        this.contributionRepositoryPort = contributionRepositoryPort;
        this.poolRepositoryPort = poolRepositoryPort;
    }

    @Override
    public Contribution execute(CreateContributionCommand command) {
        // 1. Verifier l'existence de la cagnotte
        Pool pool = poolRepositoryPort.findById(command.poolId())
                .orElseThrow(() -> new PoolNotFoundException("Cagnotte non trouvee : " + command.poolId()));

        // 2. Creer la contribution
        Contribution contribution = Contribution.builder()
                .id(UUID.randomUUID().toString())
                .poolId(command.poolId())
                .userId(command.userId())
                .contributorEmail(command.contributorEmail())
                .contributorName(command.contributorName())
                .amount(command.amount())
                .message(command.message())
                .anonymous(command.anonymous())
                .paymentMethod(command.paymentMethod())
                .status(ContributionStatus.REUSSIE) // On simule un succès direct pour l'instant
                .createdAt(Instant.now())
                .validatedAt(Instant.now())
                .build();

        Contribution savedContribution = contributionRepositoryPort.save(contribution);

        // 3. Mettre a jour le montant de la cagnotte si la contribution est reussie
        if (savedContribution.isSuccessful()) {
            pool.setCurrentAmount(pool.getCurrentAmount().add(savedContribution.getAmount()));
            poolRepositoryPort.save(pool);
        }

        return savedContribution;
    }
}
