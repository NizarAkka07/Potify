package com.alphateckplus.potify.pool.application_service.primary.pool.get_pool;

import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Pool;
import java.util.Optional;

/**
 * Implementation par defaut du service de recuperation de cagnotte.
 */
public class DefaultGetPoolService implements GetPoolService {

    private final PoolRepositoryPort poolRepositoryPort;

    public DefaultGetPoolService(PoolRepositoryPort poolRepositoryPort) {
        this.poolRepositoryPort = poolRepositoryPort;
    }

    @Override
    public Optional<Pool> execute(String id) {
        return poolRepositoryPort.findById(id);
    }
}
