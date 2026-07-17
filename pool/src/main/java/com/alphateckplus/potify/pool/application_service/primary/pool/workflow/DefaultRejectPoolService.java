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
public class DefaultRejectPoolService implements RejectPoolService {

    private final PoolRepositoryPort poolRepositoryPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pool execute(String id, String reason) throws Exception {
        log.info("Rejecting pool {} by moderator/admin, reason: {}", id, reason);

        Pool pool = poolRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable : " + id));

        if (pool.getStatus() != PoolStatus.PUBLIEE) {
            throw new IllegalStateException("Seules les cagnottes actives peuvent être rejetées. Statut actuel: " + pool.getStatus());
        }

        // Suspend la cagnotte suite au rejet
        pool.setStatus(PoolStatus.SUSPENDUE);
        pool.setUpdatedAt(Instant.now());
        Pool savedPool = poolRepositoryPort.save(pool);

        // Rejeter récursivement les sous-cagnottes enfants
        if (pool.getChildren() != null && !pool.getChildren().isEmpty()) {
            for (Pool child : pool.getChildren()) {
                child.setStatus(PoolStatus.SUSPENDUE);
                child.setUpdatedAt(Instant.now());
                poolRepositoryPort.save(child);
            }
        }

        // Notifier le créateur avec le motif de rejet
        notificationEventPublisherPort.publish(
                pool.getOwnerId(),
                "POOL_REJECTED",
                "Cagnotte rejetée",
                "Votre cagnotte '" + pool.getTitle() + "' a été rejetée par la modération. Motif: " + reason
        );

        log.info("Pool {} successfully rejected and reset to SUSPENDUE", id);
        return savedPool;
    }
}
