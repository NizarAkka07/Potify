package com.alphateckplus.potify.pool.application_service.primary.contribution.create_contribution;

import com.alphateckplus.potify.pool.application_service.primary.command.CreateContributionCommand;
import com.alphateckplus.potify.pool.application_service.secondary.contribution.ContributionRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.WalletRepositoryPort;
import com.alphateckplus.potify.pool.domain.exception.PoolNotFoundException;
import com.alphateckplus.potify.pool.domain.model.Contribution;
import com.alphateckplus.potify.pool.domain.model.ContributionStatus;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.domain.model.Wallet;
import java.time.Instant;

/**
 * Implementation par defaut du service de creation de contribution.
 */
public class DefaultCreateContributionService implements CreateContributionService {

    private final ContributionRepositoryPort contributionRepositoryPort;
    private final PoolRepositoryPort poolRepositoryPort;
    private final WalletRepositoryPort walletRepositoryPort;

    public DefaultCreateContributionService(
            ContributionRepositoryPort contributionRepositoryPort,
            PoolRepositoryPort poolRepositoryPort,
            WalletRepositoryPort walletRepositoryPort) {
        this.contributionRepositoryPort = contributionRepositoryPort;
        this.poolRepositoryPort = poolRepositoryPort;
        this.walletRepositoryPort = walletRepositoryPort;
    }

    @Override
    public Contribution execute(CreateContributionCommand command) {
        // 1. Verifier l'existence de la cagnotte et l'acces
        Pool pool = poolRepositoryPort.findById(command.poolId())
                .orElseThrow(() -> new PoolNotFoundException("Cagnotte non trouvee : " + command.poolId()));

        if (!pool.isUserAllowed(command.userId(), command.contributorEmail())) {
            throw new IllegalStateException("Vous n'etes pas autorise a contribuer a cette cagnotte privee.");
        }

        // 2. Creer la contribution
        Contribution contribution = Contribution.builder()
                .poolId(command.poolId())
                .userId(command.userId())
                .contributorEmail(command.contributorEmail())
                .contributorName(command.contributorName())
                .amount(command.amount())
                .message(command.message())
                .anonymous(command.anonymous())
                .paymentMethod(command.paymentMethod())
                .status(ContributionStatus.REUSSIE) // Simule un succès direct
                .createdAt(Instant.now())
                .validatedAt(Instant.now())
                .build();

        Contribution savedContribution = contributionRepositoryPort.save(contribution);

        // 3. Mises a jour si la contribution est reussie
        if (savedContribution.isSuccessful()) {
            // Mise a jour du montant global de la cagnotte
            pool.setCurrentAmount(pool.getCurrentAmount().add(savedContribution.getAmount()));
            poolRepositoryPort.save(pool);
            
            // Mise a jour du Wallet (Solde disponible)
            walletRepositoryPort.findByPoolId(pool.getId()).ifPresent(wallet -> {
                wallet.setAvailableBalance(wallet.getAvailableBalance().add(savedContribution.getAmount()));
                walletRepositoryPort.save(wallet);
            });
        }

        return savedContribution;
    }
}
