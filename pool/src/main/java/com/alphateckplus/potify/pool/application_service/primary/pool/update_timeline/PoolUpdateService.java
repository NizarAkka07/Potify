package com.alphateckplus.potify.pool.application_service.primary.pool.update_timeline;

import com.alphateckplus.potify.pool.domain.model.PoolUpdate;
import java.util.List;

/**
 * Port d'entrée pour la gestion des actualités et timeline de confiance d'une cagnotte.
 */
public interface PoolUpdateService {

    /**
     * Publie une nouvelle mise à jour pour une cagnotte et notifie tous les contributeurs.
     *
     * @param poolId L'ID de la cagnotte.
     * @param poolUpdate Les informations de la mise à jour.
     * @param currentUserId L'ID de l'utilisateur effectuant l'action (doit être le créateur de la cagnotte).
     * @return La mise à jour créée.
     */
    PoolUpdate createUpdate(String poolId, PoolUpdate poolUpdate, String currentUserId);

    /**
     * Récupère la liste des mises à jour pour une cagnotte.
     *
     * @param poolId L'ID de la cagnotte.
     * @return La liste des mises à jour triées par ordre antéchronologique.
     */
    List<PoolUpdate> getUpdatesByPoolId(String poolId);
}
