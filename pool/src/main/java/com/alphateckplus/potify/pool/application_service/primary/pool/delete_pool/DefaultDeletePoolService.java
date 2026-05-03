package com.alphateckplus.potify.pool.application_service.primary.pool.delete_pool;

import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;

/**
 * Implementation par defaut du service de suppression de cagnotte.
 */
public class DefaultDeletePoolService implements DeletePoolService {

    private final PoolRepositoryPort poolRepositoryPort;

    public DefaultDeletePoolService(PoolRepositoryPort poolRepositoryPort) {
        this.poolRepositoryPort = poolRepositoryPort;
    }

    @Override
    public void execute(String id) {
        // Logique de suppression simple pour l'instant.
        // La regle RM-10 sur la suppression logique sera affinee lors de l'ajout des contributions.
        poolRepositoryPort.deleteById(id);
    }
}
