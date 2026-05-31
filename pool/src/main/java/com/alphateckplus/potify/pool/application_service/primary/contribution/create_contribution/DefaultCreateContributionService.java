package com.alphateckplus.potify.pool.application_service.primary.contribution.create_contribution;

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
    public Contribution execute(Contribution contribution) {
        // 1. Verifier l'existence de la cagnotte et l'acces
        Pool pool = poolRepositoryPort.findById(contribution.getPoolId())
                .orElseThrow(() -> new PoolNotFoundException("Cagnotte non trouvee : " + contribution.getPoolId()));

        if (!pool.isUserAllowed(contribution.getUserId(), contribution.getContributorEmail())) {
            throw new IllegalStateException("Vous n'etes pas autorise a contribuer a cette cagnotte privee.");
        }

        if (pool.getParentId() == null && pool.getChildren() != null && !pool.getChildren().isEmpty()) {
            throw new IllegalStateException("Cette cagnotte principale contient des sous-cagnottes. Les contributions doivent être ciblées sur l'une d'elles.");
        }

        Pool targetPool = pool;

        // 2. Mettre a jour les informations de la contribution
        contribution.setStatus(ContributionStatus.REUSSIE); // Simule un succès direct
        contribution.setCreatedAt(Instant.now());
        contribution.setValidatedAt(Instant.now());

        Contribution savedContribution = contributionRepositoryPort.save(contribution);

        // 3. Mises a jour si la contribution est reussie
        if (savedContribution.isSuccessful()) {
            // Mise a jour du montant global de la sous-cagnotte et recalcul des phases
            targetPool.addContributionAmount(savedContribution.getAmount());
            poolRepositoryPort.save(targetPool);
            
            // Mise a jour du montant de la cagnotte principale associée (uniquement si c'est une sous-cagnotte)
            if (targetPool.getParentId() != null) {
                poolRepositoryPort.findById(targetPool.getParentId()).ifPresent(parent -> {
                    parent.setCurrentAmount((parent.getCurrentAmount() != null ? parent.getCurrentAmount() : java.math.BigDecimal.ZERO).add(savedContribution.getAmount()));
                    poolRepositoryPort.save(parent);
                });
            }
            
            // Mise a jour du Wallet de la sous-cagnotte
            walletRepositoryPort.findByPoolId(targetPool.getId()).ifPresent(wallet -> {
                wallet.setAvailableBalance(wallet.getAvailableBalance().add(savedContribution.getAmount()));
                walletRepositoryPort.save(wallet);
            });
        }

        return savedContribution;
    }
}
