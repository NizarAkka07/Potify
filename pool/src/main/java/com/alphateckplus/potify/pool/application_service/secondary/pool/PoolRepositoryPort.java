package com.alphateckplus.potify.pool.application_service.secondary.pool;

import com.alphateckplus.potify.pool.domain.model.Pool;
import java.util.List;
import java.util.Optional;

/**
 * Port secondaire pour la persistence des cagnottes.
 */
public interface PoolRepositoryPort {

    /**
     * Sauvegarde une cagnotte (creation ou mise a jour).
     *
     * @param pool La cagnotte a sauvegarder.
     * @return La cagnotte sauvegardee.
     */
    Pool save(Pool pool);

    /**
     * Recherche une cagnotte par son identifiant.
     *
     * @param id L'identifiant de la cagnotte.
     * @return Un Optional contenant la cagnotte si trouvee.
     */
    Optional<Pool> findById(String id);

    /**
     * Liste toutes les cagnottes.
     *
     * @return La liste des cagnottes.
     */
    List<Pool> findAll();

    /**
     * Supprime une cagnotte.
     *
     * @param id L'identifiant de la cagnotte.
     */
    void deleteById(String id);
}
