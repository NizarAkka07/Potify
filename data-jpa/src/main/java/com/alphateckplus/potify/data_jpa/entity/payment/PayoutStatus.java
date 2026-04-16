package com.alphateckplus.potify.data_jpa.entity.payment;

/**
 * Statut de paiement sortant (decaissement).
 */
public enum PayoutStatus {
    REQUESTED,
    PROCESSING,
    COMPLETED,
    REJECTED
}
