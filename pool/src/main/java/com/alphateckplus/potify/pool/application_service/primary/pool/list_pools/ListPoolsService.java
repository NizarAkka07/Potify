package com.alphateckplus.potify.pool.application_service.primary.pool.list_pools;

import com.alphateckplus.potify.pool.domain.model.Pool;
import java.util.List;

/**
 * Port d'entree pour lister les cagnottes.
 */
public interface ListPoolsService {

    /**
     * Liste toutes les cagnottes accessibles.
     *
     * @return La liste des cagnottes.
     */
    List<Pool> execute();
}
