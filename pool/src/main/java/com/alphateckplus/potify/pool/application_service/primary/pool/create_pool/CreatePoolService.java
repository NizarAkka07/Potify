package com.alphateckplus.potify.pool.application_service.primary.pool.create_pool;

import com.alphateckplus.potify.pool.application_service.primary.command.CreatePoolCommand;
import com.alphateckplus.potify.pool.domain.model.Pool;

/**
 * Port d'entree pour creer une cagnotte.
 */
public interface CreatePoolService {

    /**
     * Cree une nouvelle cagnotte selon les regles métier.
     *
     * @param command Les donnees de creation.
     * @return La cagnotte creee.
     */
    Pool execute(CreatePoolCommand command);
}
