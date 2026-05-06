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

    /**
     * Liste toutes les cagnottes publiques.
     *
     * @return La liste des cagnottes publiques.
     */
    List<Pool> listPublicPools();

    /**
     * Recherche des cagnottes publiques.
     *
     * @param query Le terme de recherche.
     * @return La liste des cagnottes publiques correspondantes.
     */
    List<Pool> searchPublicPools(String query);
}
