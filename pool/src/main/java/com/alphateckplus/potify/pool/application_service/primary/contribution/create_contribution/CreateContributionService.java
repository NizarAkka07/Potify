package com.alphateckplus.potify.pool.application_service.primary.contribution.create_contribution;

import com.alphateckplus.potify.pool.application_service.primary.command.CreateContributionCommand;
import com.alphateckplus.potify.pool.domain.model.Contribution;

/**
 * Port d'entree pour creer une contribution.
 */
public interface CreateContributionService {

    /**
     * Cree une contribution et met a jour la cagnotte si necessaire.
     *
     * @param command Les donnees de la contribution.
     * @return La contribution creee.
     */
    Contribution execute(CreateContributionCommand command);
}
