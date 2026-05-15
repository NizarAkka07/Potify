package com.alphateckplus.potify.pool.application_service.primary.pool.create_pool;

import com.alphateckplus.potify.pool.application_service.primary.command.CreatePoolCommand;
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
    public Pool execute(CreatePoolCommand command) {
        Pool pool = Pool.builder()
                .ownerId(command.ownerId())
                .title(command.title())
                .description(command.description())
                .category(command.category())
                .goalAmount(command.goalAmount())
                .currentAmount(BigDecimal.ZERO)
                .status(PoolStatus.PUBLIEE)
                .type(command.type())
                .invitedUserIds(command.invitedUserIds())
                .imageContent(command.imageContent())
                .imageContentType(command.imageContentType())
                .videoUrl(command.videoUrl())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Pool savedPool = poolRepositoryPort.save(pool);
        
        // Création automatique du wallet associé
        walletRepositoryPort.save(Wallet.createEmpty(savedPool.getId()));
        
        return savedPool;
    }
}
