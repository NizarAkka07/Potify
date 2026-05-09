package com.alphateckplus.potify.user.domain.model;

/**
 * Statut metier de l'utilisateur dans le domaine user.
 *
 * <p>On garde cet enum dans le domaine pour eviter de coupler la logique metier
 * aux details techniques de la couche de persistance.
 */
public enum UserStatus {
    ACTIVE,
    PENDING_VERIFICATION,
    SUSPENDED,
    DELETED
}
