package com.alphateckplus.potify.pool.application_service.primary.contribution.list_contributions;

import com.alphateckplus.potify.pool.domain.model.Contribution;
import java.util.List;

/**
 * Port d'entree pour lister les contributions.
 */
public interface ListContributionsService {

    /**
     * Liste les contributions d'un utilisateur.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @return La liste des contributions.
     */
    List<Contribution> findByUserId(String userId);

    /**
     * Liste les contributions pour une cagnotte.
     *
     * @param poolId L'identifiant de la cagnotte.
     * @return La liste des contributions.
     */
    List<Contribution> findByPoolId(String poolId);
}
