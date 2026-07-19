package com.alphateckplus.potify.pool.application_service.primary.pool.update_timeline;

import com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolUpdateRepositoryPort;
import com.alphateckplus.potify.pool.domain.exception.PoolNotFoundException;
import com.alphateckplus.potify.pool.domain.model.ContributorInfo;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.domain.model.PoolUpdate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation par défaut du service de timeline de confiance.
 */
@RequiredArgsConstructor
@Slf4j
public class DefaultPoolUpdateService implements PoolUpdateService {

    private final PoolUpdateRepositoryPort poolUpdateRepositoryPort;
    private final PoolRepositoryPort poolRepositoryPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;

    @Override
    public PoolUpdate createUpdate(String poolId, PoolUpdate poolUpdate, String currentUserId) {
        log.info("Creating update for poolId={} by userId={}", poolId, currentUserId);
        
        Pool pool = poolRepositoryPort.findById(poolId)
                .orElseThrow(() -> new PoolNotFoundException("Cagnotte introuvable avec l'ID: " + poolId));

        // Vérification de sécurité : Seul le propriétaire de la cagnotte peut publier une actualité
        if (!pool.getOwnerId().equals(currentUserId)) {
            log.error("Access denied: User {} is not the owner of pool {}", currentUserId, poolId);
            throw new IllegalArgumentException("Seul le créateur de la cagnotte peut publier une mise à jour.");
        }

        poolUpdate.setPoolId(poolId);
        PoolUpdate savedUpdate = poolUpdateRepositoryPort.save(poolUpdate);
        log.info("Update created successfully with id={}", savedUpdate.getId());

        // Notification asynchrone des contributeurs
        try {
            List<ContributorInfo> contributors = poolUpdateRepositoryPort.findContributorsByPoolId(poolId);
            log.info("Found {} unique contributors to notify for poolId={}", contributors.size(), poolId);

            String notificationTitle = "Nouvelle actualité sur la cagnotte : " + pool.getTitle();
            String notificationContent = "Le créateur a publié une mise à jour :\n\n"
                    + savedUpdate.getTitle() + "\n\n"
                    + savedUpdate.getContent();

            for (ContributorInfo contributor : contributors) {
                // Si le contributeur est l'owner lui-même (peut arriver s'il s'est fait un don), on ne le notifie pas
                if (contributor.getUserId() != null && contributor.getUserId().equals(currentUserId)) {
                    continue;
                }
                
                log.debug("Notifying contributor: userId={}, email={}", contributor.getUserId(), contributor.getEmail());
                notificationEventPublisherPort.publish(
                        contributor.getUserId(),
                        contributor.getEmail(),
                        "POOL_UPDATE",
                        notificationTitle,
                        notificationContent
                );
            }
        } catch (Exception e) {
            log.error("Failed to notify contributors about pool update", e);
        }

        return savedUpdate;
    }

    @Override
    public List<PoolUpdate> getUpdatesByPoolId(String poolId) {
        log.info("Fetching updates for poolId={}", poolId);
        // On s'assure d'abord que la cagnotte existe
        if (!poolRepositoryPort.findById(poolId).isPresent()) {
            throw new PoolNotFoundException("Cagnotte introuvable avec l'ID: " + poolId);
        }
        return poolUpdateRepositoryPort.findByPoolId(poolId);
    }
}
