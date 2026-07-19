package com.alphateckplus.potify.pool.application_service.primary.pool.update_pool;

import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.UserCheckPort;
import com.alphateckplus.potify.pool.domain.exception.PoolNotFoundException;
import com.alphateckplus.potify.pool.domain.model.Pool;
import java.time.Instant;

/**
 * Implementation par defaut du service de mise a jour de cagnotte.
 */
public class DefaultUpdatePoolService implements UpdatePoolService {

    private final PoolRepositoryPort poolRepositoryPort;
    private final UserCheckPort userCheckPort;

    public DefaultUpdatePoolService(PoolRepositoryPort poolRepositoryPort, UserCheckPort userCheckPort) {
        this.poolRepositoryPort = poolRepositoryPort;
        this.userCheckPort = userCheckPort;
    }

    @Override
    public Pool execute(Pool poolToUpdate) {
        Pool pool = poolRepositoryPort.findById(poolToUpdate.getId())
                .orElseThrow(() -> new PoolNotFoundException("Cagnotte non trouvee avec l'id : " + poolToUpdate.getId()));

        // Verification de l'ownership (stateless via SecurityContext)
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé : Utilisateur non authentifié");
        }
        
        String currentUserEmail = auth.getName();
        String ownerEmail = userCheckPort.getEmailById(pool.getOwnerId());
        
        boolean isOwner = currentUserEmail != null && currentUserEmail.equalsIgnoreCase(ownerEmail);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_POOL") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
                
        if (!isOwner && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé : Vous n'êtes pas le propriétaire de cette cagnotte");
        }

        if (poolToUpdate.getTitle() != null) pool.setTitle(poolToUpdate.getTitle());
        if (poolToUpdate.getDescription() != null) pool.setDescription(poolToUpdate.getDescription());
        if (poolToUpdate.getGoalAmount() != null) pool.setGoalAmount(poolToUpdate.getGoalAmount());
        if (poolToUpdate.getStatus() != null) {
            pool.setStatus(poolToUpdate.getStatus());
            if (poolToUpdate.getStatus() == com.alphateckplus.potify.pool.domain.model.PoolStatus.PUBLIEE) {
                poolRepositoryPort.clearReports(pool.getId());
            }
        }
        if (poolToUpdate.getVideoContent() != null) pool.setVideoContent(poolToUpdate.getVideoContent());
        if (poolToUpdate.getVideoContentType() != null) pool.setVideoContentType(poolToUpdate.getVideoContentType());
        if (poolToUpdate.getVideoUrl() != null) pool.setVideoUrl(poolToUpdate.getVideoUrl());
        if (poolToUpdate.getImageContent() != null) pool.setImageContent(poolToUpdate.getImageContent());
        if (poolToUpdate.getImageContentType() != null) pool.setImageContentType(poolToUpdate.getImageContentType());
        if (poolToUpdate.getImageUrl() != null) pool.setImageUrl(poolToUpdate.getImageUrl());
        if (poolToUpdate.getFees() != null) pool.setFees(poolToUpdate.getFees());

        pool.setUpdatedAt(Instant.now());

        return poolRepositoryPort.save(pool);
    }
}
