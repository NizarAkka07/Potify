package com.alphateckplus.potify.pool.application_service.primary.pool.list_pools;

import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Pool;
import java.util.List;

/**
 * Implementation par defaut du service de liste des cagnottes.
 */
public class DefaultListPoolsService implements ListPoolsService {

    private final PoolRepositoryPort poolRepositoryPort;

    public DefaultListPoolsService(PoolRepositoryPort poolRepositoryPort) {
        this.poolRepositoryPort = poolRepositoryPort;
    }

    @Override
    public List<Pool> execute() {
        return poolRepositoryPort.findAll();
    }
}
