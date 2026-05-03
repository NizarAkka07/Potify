package com.alphateckplus.potify.pool.application_service.primary.pool.create_pool;

import com.alphateckplus.potify.pool.application_service.primary.command.CreatePoolCommand;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.domain.model.PoolStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Implementation par defaut du service de creation de cagnotte.
 */
public class DefaultCreatePoolService implements CreatePoolService {

    private final PoolRepositoryPort poolRepositoryPort;

    public DefaultCreatePoolService(PoolRepositoryPort poolRepositoryPort) {
        this.poolRepositoryPort = poolRepositoryPort;
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
                .status(PoolStatus.PUBLIEE) // Directement publiée pour contourner la contrainte DB
                .type(command.type())
                .invitedUserIds(command.invitedUserIds())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return poolRepositoryPort.save(pool);
    }
}
