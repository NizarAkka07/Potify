package com.alphateckplus.potify.pool.application_service.primary.pool.create_pool;

import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.WalletRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.domain.model.PoolStatus;
import com.alphateckplus.potify.pool.domain.model.Wallet;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Implementation par defaut du service de creation de cagnotte.
 */
public class DefaultCreatePoolService implements CreatePoolService {

    private final PoolRepositoryPort poolRepositoryPort;
    private final WalletRepositoryPort walletRepositoryPort;

    public DefaultCreatePoolService(PoolRepositoryPort poolRepositoryPort, WalletRepositoryPort walletRepositoryPort) {
        this.poolRepositoryPort = poolRepositoryPort;
        this.walletRepositoryPort = walletRepositoryPort;
    }

    @Override
    public Pool execute(Pool pool) {
        BigDecimal mainGoal = BigDecimal.ZERO;
        if (pool.getChildren() != null && !pool.getChildren().isEmpty()) {
            for (Pool subPool : pool.getChildren()) {
                BigDecimal subPoolGoal = subPool.getPhases().stream()
                        .map(com.alphateckplus.potify.pool.domain.model.Phase::getGoalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                mainGoal = mainGoal.add(subPoolGoal);
            }
        } else if (pool.getPhases() != null && !pool.getPhases().isEmpty()) {
            mainGoal = pool.getPhases().stream()
                    .map(com.alphateckplus.potify.pool.domain.model.Phase::getGoalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            mainGoal = pool.getGoalAmount() != null ? pool.getGoalAmount() : BigDecimal.ZERO;
        }

        pool.setGoalAmount(mainGoal);
        pool.setCurrentAmount(BigDecimal.ZERO);
        pool.setStatus(PoolStatus.PUBLIEE);
        pool.setCreatedAt(Instant.now());
        pool.setUpdatedAt(Instant.now());
        if (pool.getHasDeadline() == null) {
            pool.setHasDeadline(false);
        }

        if (pool.getPhases() != null && !pool.getPhases().isEmpty()) {
            for (com.alphateckplus.potify.pool.domain.model.Phase phase : pool.getPhases()) {
                phase.setStatus(com.alphateckplus.potify.pool.domain.model.PhaseStatus.ACTIVE);
            }
        }

        Pool savedPool = poolRepositoryPort.save(pool);
        // Initialiser le portefeuille financier pour la cagnotte publiée directement
        walletRepositoryPort.save(Wallet.createEmpty(savedPool.getId()));

        if (pool.getChildren() != null && !pool.getChildren().isEmpty()) {
            for (Pool subPool : pool.getChildren()) {
                if (subPool.getPhases() != null && !subPool.getPhases().isEmpty()) {
                    for (com.alphateckplus.potify.pool.domain.model.Phase phase : subPool.getPhases()) {
                        phase.setStatus(com.alphateckplus.potify.pool.domain.model.PhaseStatus.ACTIVE);
                    }
                }
                BigDecimal subGoal = subPool.getPhases() != null ? subPool.getPhases().stream()
                        .map(com.alphateckplus.potify.pool.domain.model.Phase::getGoalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;

                subPool.setOwnerId(savedPool.getOwnerId());
                subPool.setCategory(savedPool.getCategory());
                subPool.setGoalAmount(subGoal);
                subPool.setCurrentAmount(BigDecimal.ZERO);
                subPool.setStatus(PoolStatus.PUBLIEE);
                subPool.setType(savedPool.getType());
                subPool.setParentId(savedPool.getId());
                if (subPool.getHasDeadline() == null) {
                    subPool.setHasDeadline(false);
                }
                subPool.setCreatedAt(Instant.now());
                subPool.setUpdatedAt(Instant.now());

                Pool savedSubPool = poolRepositoryPort.save(subPool);
                // Initialiser le portefeuille financier pour la sous-cagnotte
                walletRepositoryPort.save(Wallet.createEmpty(savedSubPool.getId()));
            }
        }
        
        return savedPool;
    }
}

