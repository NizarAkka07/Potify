package com.alphateckplus.potify.pool.domain.model;

/**
 * Type de cagnotte defini dans le domaine pool.
 *
 * <p>Permet de distinguer les collectes publiques standards des cercles prives.
 */
public enum PoolType {
    /** Collecte publique ouverte a tous les visiteurs. */
    PUBLIC,

    /**
     * Cagnotte privee de type Tontine.
     * Accessible uniquement sur invitation pour un cercle restreint de membres.
     */
    PRIVATE_TONTINE
}
