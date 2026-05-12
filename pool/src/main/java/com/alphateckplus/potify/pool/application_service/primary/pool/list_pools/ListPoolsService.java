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

    /**
     * Liste les cagnottes d'un utilisateur.
     *
     * @param ownerId L'identifiant de l'utilisateur.
     * @return La liste des cagnottes.
     */
    List<Pool> findByOwnerId(String ownerId);

    /**
     * Liste les cagnottes d'un utilisateur.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param email L'email de l'utilisateur.
     * @return La liste des cagnottes.
     */
    List<Pool> findByUser(String userId, String email);
}
