package com.alphateckplus.potify.pool.application_service.primary.pool.delete_pool;

/**
 * Port d'entree pour supprimer ou archiver une cagnotte.
 */
public interface DeletePoolService {

    /**
     * Supprime ou archive une cagnotte selon les regles metier.
     *
     * @param id L'identifiant de la cagnotte a supprimer.
     */
    void execute(String id);
}
