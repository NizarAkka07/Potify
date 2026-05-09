package com.alphateckplus.potify.pool.application_service.primary.pool.get_pool;

import com.alphateckplus.potify.pool.domain.model.Pool;
import java.util.Optional;

/**
 * Port d'entree pour recuperer une cagnotte.
 */
public interface GetPoolService {

    /**
     * Recupere une cagnotte par son identifiant.
     *
     * @param id L'identifiant de la cagnotte.
     * @return Un Optional contenant la cagnotte si trouvee.
     */
    Optional<Pool> execute(String id);
    Optional<Pool> execute(String id, String userId, String email);
}
