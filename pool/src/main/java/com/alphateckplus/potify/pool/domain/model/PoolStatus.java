package com.alphateckplus.potify.pool.domain.model;

/**
 * Statut metier d'une cagnotte (Pool) dans le domaine pool.
 *
 * <p>Ces statuts definissent le cycle de vie d'une cagnotte et les actions autorisees.
 */
public enum PoolStatus {
    /** La cagnotte est validee et visible publiquement ou par les invites. */
    PUBLIEE,

    /** La cagnotte est temporairement suspendue par un administrateur. */
    SUSPENDUE,

    /** La cagnotte est terminee, elle n'accepte plus de contributions. */
    CLOTUREE,

    /** La cagnotte est archivee et n'est plus visible dans les listes actives. */
    ARCHIVEE
}
