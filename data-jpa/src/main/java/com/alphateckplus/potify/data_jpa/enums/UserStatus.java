package com.alphateckplus.potify.data_jpa.enums;

/**
 * Statut fonctionnel d'un utilisateur.
 *
 * <p>On stocke un enum (et pas une String libre) pour eviter les fautes de frappe,
 * fiabiliser les regles metier et faciliter les validations.
 */
public enum UserStatus {
    ACTIVE,
    SUSPENDED,
    DELETED
}
