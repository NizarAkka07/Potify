package com.alphateckplus.potify.pool.application_service.primary.pool.create_pool;

import com.alphateckplus.potify.pool.domain.model.Pool;

/**
 * Port d'entree pour creer une cagnotte.
 */
public interface CreatePoolService {

    /**
     * Cree une nouvelle cagnotte selon les regles métier.
     *
     * @param pool La cagnotte a creer.
     * @return La cagnotte creee.
     */
    Pool execute(Pool pool);
}

