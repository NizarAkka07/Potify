package com.alphateckplus.potify.pool.application_service.primary.pool.update_pool;

import com.alphateckplus.potify.pool.application_service.primary.command.UpdatePoolCommand;
import com.alphateckplus.potify.pool.domain.model.Pool;

/**
 * Port d'entree pour mettre a jour une cagnotte.
 */
public interface UpdatePoolService {

    /**
     * Met a jour une cagnotte existante.
     *
     * @param command Les donnees de mise a jour.
     * @return La cagnotte mise a jour.
     */
    Pool execute(UpdatePoolCommand command);
}
