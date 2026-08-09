package com.alphateckplus.potify.support.infrastructure.secondary.presence;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AdminPresenceRegistry {

    private final Map<String, Instant> activeAdmins = new ConcurrentHashMap<>();
    private static final Duration OFFLINE_TIMEOUT = Duration.ofSeconds(30);

    public void registerAdminActivity(String adminIdentifier) {
        if (adminIdentifier != null && !adminIdentifier.isBlank()) {
            boolean wasEmpty = activeAdmins.isEmpty();
            activeAdmins.put(adminIdentifier, Instant.now());
            if (wasEmpty) {
                log.info("Premier administrateur connecté : {}. Statut support -> EN LIGNE", adminIdentifier);
            }
        }
    }

    public void unregisterAdmin(String adminIdentifier) {
        if (adminIdentifier != null) {
            activeAdmins.remove(adminIdentifier);
            log.info("Administrateur déconnecté : {}", adminIdentifier);
            cleanExpiredSessions();
            if (activeAdmins.isEmpty()) {
                log.info("Aucun administrateur connecté. Statut support -> HORS LIGNE");
            }
        }
    }

    public boolean isAnyAdminOnline() {
        cleanExpiredSessions();
        boolean online = !activeAdmins.isEmpty();
        log.debug("Vérification présence admin: {} (Actifs: {})", online, activeAdmins.keySet());
        return online;
    }

    public int getOnlineAdminCount() {
        cleanExpiredSessions();
        return activeAdmins.size();
    }

    private void cleanExpiredSessions() {
        Instant now = Instant.now();
        activeAdmins.entrySet().removeIf(entry -> {
            boolean expired = Duration.between(entry.getValue(), now).compareTo(OFFLINE_TIMEOUT) > 0;
            if (expired) {
                log.info("Session admin expirée (timeout): {}", entry.getKey());
            }
            return expired;
        });
    }
}
