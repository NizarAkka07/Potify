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

    @Override
    public List<Pool> listPublicPools() {
        return poolRepositoryPort.findPublicPools();
    }

    @Override
    public List<Pool> searchPublicPools(String query) {
        if (query == null || query.isBlank()) {
            return listPublicPools();
        }
        return poolRepositoryPort.searchPublicPools(query);
    }

    @Override
    public List<Pool> findByOwnerId(String ownerId) {
        return poolRepositoryPort.findByOwnerId(ownerId);
    }

    @Override
    public List<Pool> findByUser(String userId, String email) {
        return poolRepositoryPort.findByUser(userId, email);
    }

    @Override
    public List<Pool> findInvitedPools(String userId, String email) {
        return poolRepositoryPort.findInvitedPools(userId, email);
    }
}
