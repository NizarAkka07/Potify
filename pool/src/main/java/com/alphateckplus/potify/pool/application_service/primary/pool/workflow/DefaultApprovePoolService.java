package com.alphateckplus.potify.pool.application_service.primary.pool.workflow;

import com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.WalletRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.domain.model.PoolStatus;
import com.alphateckplus.potify.pool.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
public class DefaultApprovePoolService implements ApprovePoolService {

    private final PoolRepositoryPort poolRepositoryPort;
    private final WalletRepositoryPort walletRepositoryPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pool execute(String id) throws Exception {
        return execute(id, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pool execute(String id, String customTitle, String customMessage) throws Exception {
        log.info("Approving pool {} by moderator/admin", id);

        Pool pool = poolRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable : " + id));

        if (pool.getStatus() != PoolStatus.PUBLIEE && pool.getStatus() != PoolStatus.SUSPENDUE) {
            throw new IllegalStateException("Seules les cagnottes actives ou suspendues peuvent être approuvées. Statut actuel: " + pool.getStatus());
        }

        pool.setStatus(PoolStatus.PUBLIEE);
        pool.setUpdatedAt(Instant.now());
        Pool savedPool = poolRepositoryPort.save(pool);

        // Supprimer les signalements puisque la cagnotte a été approuvée/rétablie par la modération
        poolRepositoryPort.clearReports(id);

        // Créer le CagnotteWalletEntity associé s'il n'existe pas déjà
        if (walletRepositoryPort.findByPoolId(savedPool.getId()).isEmpty()) {
            walletRepositoryPort.save(Wallet.createEmpty(savedPool.getId()));
            log.info("Financial wallet successfully initialized for approved pool {}", id);
        } else {
            log.info("Financial wallet already exists for pool {}, skipping creation", id);
        }

        // Approuver récursivement et créer des portefeuilles pour les sous-cagnottes enfants
        if (pool.getChildren() != null && !pool.getChildren().isEmpty()) {
            for (Pool child : pool.getChildren()) {
                child.setStatus(PoolStatus.PUBLIEE);
                child.setUpdatedAt(Instant.now());
                Pool savedChild = poolRepositoryPort.save(child);
                if (walletRepositoryPort.findByPoolId(savedChild.getId()).isEmpty()) {
                    walletRepositoryPort.save(Wallet.createEmpty(savedChild.getId()));
                }
            }
        }

        String finalTitle = (customTitle != null && !customTitle.isBlank())
                ? customTitle
                : "Cagnotte approuvée / réactivée ! 🎉";

        String finalMessage = (customMessage != null && !customMessage.isBlank())
                ? customMessage
                : "Félicitations, votre cagnotte '" + pool.getTitle() + "' a été validée par la modération et est désormais active.";

        // Envoyer la notification au propriétaire
        if (pool.getOwnerId() != null && !pool.getOwnerId().isBlank()) {
            notificationEventPublisherPort.publish(
                    pool.getOwnerId(),
                    "POOL_PUBLISHED",
                    finalTitle,
                    finalMessage
            );
        }

        log.info("Pool {} successfully approved and published", id);
        return savedPool;
    }
}
