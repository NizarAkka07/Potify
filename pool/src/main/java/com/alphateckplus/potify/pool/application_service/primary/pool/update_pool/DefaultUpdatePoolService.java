package com.alphateckplus.potify.pool.application_service.primary.pool.update_pool;

import com.alphateckplus.potify.pool.application_service.primary.command.UpdatePoolCommand;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.domain.exception.PoolNotFoundException;
import com.alphateckplus.potify.pool.domain.model.Pool;
import java.time.Instant;

/**
 * Implementation par defaut du service de mise a jour de cagnotte.
 */
public class DefaultUpdatePoolService implements UpdatePoolService {

    private final PoolRepositoryPort poolRepositoryPort;

    public DefaultUpdatePoolService(PoolRepositoryPort poolRepositoryPort) {
        this.poolRepositoryPort = poolRepositoryPort;
    }

    @Override
    public Pool execute(UpdatePoolCommand command) {
        Pool pool = poolRepositoryPort.findById(command.id())
                .orElseThrow(() -> new PoolNotFoundException("Cagnotte non trouvee avec l'id : " + command.id()));

        if (command.title() != null) pool.setTitle(command.title());
        if (command.description() != null) pool.setDescription(command.description());
        if (command.goalAmount() != null) pool.setGoalAmount(command.goalAmount());
        if (command.status() != null) pool.setStatus(command.status());

        pool.setUpdatedAt(Instant.now());

        return poolRepositoryPort.save(pool);
    }
}
