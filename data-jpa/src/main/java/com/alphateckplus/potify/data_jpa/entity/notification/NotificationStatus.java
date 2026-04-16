package com.alphateckplus.potify.data_jpa.entity.notification;

/**
 * Etat de traitement d'une notification.
 */
public enum NotificationStatus {
    DRAFT,
    ACTIVE,
    PAUSED,
    COMPLETED,
    CANCELLED
}
