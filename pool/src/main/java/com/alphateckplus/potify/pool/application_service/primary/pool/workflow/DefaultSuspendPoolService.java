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
        log.info("Suspending pool {}, reason: {}", id, reason);

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

        // Notifier le créateur de la suspension
        notificationEventPublisherPort.publish(
                pool.getOwnerId(),
                "POOL_SUSPENDED",
                "Cagnotte suspendue",
                "Votre cagnotte '" + pool.getTitle() + "' a été suspendue. Motif: " + reason
        );

        log.info("Pool {} successfully suspended", id);
        return savedPool;
    }
}
