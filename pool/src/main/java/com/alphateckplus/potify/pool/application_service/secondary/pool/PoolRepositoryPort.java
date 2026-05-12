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
     * Liste toutes les cagnottes publiques.
     *
     * @return La liste des cagnottes publiques.
     */
    List<Pool> findPublicPools();

    /**
     * Recherche des cagnottes publiques par titre.
     *
     * @param query Le terme de recherche.
     * @return La liste des cagnottes publiques correspondantes.
     */
    List<Pool> searchPublicPools(String query);

    /**
     * Liste les cagnottes d'un utilisateur specifique.
     *
     * @param ownerId L'identifiant du proprietaire.
     * @return La liste des cagnottes.
     */
    List<Pool> findByOwnerId(String ownerId);

    /**
     * Supprime une cagnotte.
     *
     * @param id L'identifiant de la cagnotte.
     */
    void deleteById(String id);

    /**
     * Liste les cagnottes liees a un utilisateur (proprietaires ou invitees).
     */
    List<Pool> findByUser(String userId, String email);
}
