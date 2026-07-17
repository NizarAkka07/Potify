package com.alphateckplus.potify.pool.application_service.primary.pool.workflow;

import com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.UserCheckPort;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.domain.model.PoolStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
public class DefaultPublishPoolService implements PublishPoolService {

    private final PoolRepositoryPort poolRepositoryPort;
    private final UserCheckPort userCheckPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pool execute(String id) throws Exception {
        log.info("Submitting pool {} for administrative review", id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Non autorisé : Utilisateur non authentifié");
        }

        String currentUserEmail = auth.getName();
        String authenticatedUserId = userCheckPort.getIdByEmail(currentUserEmail);

        Pool pool = poolRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable : " + id));

        boolean isOwner = pool.getOwnerId() != null && pool.getOwnerId().equals(authenticatedUserId);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Non autorisé : Seul le propriétaire ou un administrateur peut soumettre la cagnotte");
        }

        if (pool.getStatus() != PoolStatus.PUBLIEE) {
            throw new IllegalStateException("Seules les cagnottes actives peuvent être soumises. Statut actuel: " + pool.getStatus());
        }

        pool.setStatus(PoolStatus.PUBLIEE);
        pool.setUpdatedAt(Instant.now());
        Pool savedPool = poolRepositoryPort.save(pool);

        // Publier l'événement Kafka potify.pool.submitted
        notificationEventPublisherPort.publish(
                pool.getOwnerId(),
                "POOL_SUBMITTED",
                "Cagnotte soumise",
                "Votre cagnotte '" + pool.getTitle() + "' a été soumise à l'équipe de modération."
        );

        log.info("Pool {} successfully submitted for review", id);
        return savedPool;
    }
}
