package com.alphateckplus.potify.pool.application_service.primary.pool.workflow;

import com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.domain.model.PoolStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
public class DefaultSuspendPoolService implements SuspendPoolService {

    private final PoolRepositoryPort poolRepositoryPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pool execute(String id, String reason) throws Exception {
        return execute(id, null, reason, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pool execute(String id, String title, String reason, String message) throws Exception {
        log.info("Suspending pool {}, title: {}, reason: {}, message: {}", id, title, reason, message);

        Pool pool = poolRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable : " + id));

        pool.setStatus(PoolStatus.SUSPENDUE);
        pool.setUpdatedAt(Instant.now());
        Pool savedPool = poolRepositoryPort.save(pool);

        // Suspendre récursivement les sous-cagnottes enfants
        if (pool.getChildren() != null && !pool.getChildren().isEmpty()) {
            for (Pool child : pool.getChildren()) {
                child.setStatus(PoolStatus.SUSPENDUE);
                child.setUpdatedAt(Instant.now());
                poolRepositoryPort.save(child);
            }
        }

        String finalTitle = (title != null && !title.isBlank())
                ? title
                : "Cagnotte suspendue par l'administration";

        String explanation = (message != null && !message.isBlank())
                ? message
                : ((reason != null && !reason.isBlank()) ? "Motif / Explication : " + reason : "Signalement ou non-conformité aux règles de la communauté.");

        String finalContent = (message != null && !message.isBlank())
                ? message
                : "Votre cagnotte '" + pool.getTitle() + "' a été suspendue par la modération. " + explanation;

        // Notifier le créateur de la suspension
        if (pool.getOwnerId() != null && !pool.getOwnerId().isBlank()) {
            notificationEventPublisherPort.publish(
                    pool.getOwnerId(),
                    "POOL_SUSPENDED",
                    finalTitle,
                    finalContent
            );
            log.info("Notification POOL_SUSPENDED envoyée à l'owner {}", pool.getOwnerId());
        } else {
            log.warn("Impossible d'envoyer la notification de suspension : pool.getOwnerId() est null pour la cagnotte {}", id);
        }

        log.info("Pool {} successfully suspended", id);
        return savedPool;
    }
}
