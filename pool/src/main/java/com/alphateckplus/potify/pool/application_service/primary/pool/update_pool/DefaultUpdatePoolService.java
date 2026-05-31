package com.alphateckplus.potify.pool.application_service.primary.pool.update_pool;

import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.domain.exception.PoolNotFoundException;
import com.alphateckplus.potify.pool.domain.model.Pool;
import java.time.Instant;

/**
 * Implementation par defaut du service de mise a jour de cagnotte.
 */
public class DefaultUpdatePoolService implements UpdatePoolService {

    private final PoolRepositoryPort poolRepositoryPort;

    public DefaultUpdatePoolService(PoolRepositoryPort poolRepositoryPort) {
        this.poolRepositoryPort = poolRepositoryPort;
    }

    @Override
    public Pool execute(Pool poolToUpdate) {
        Pool pool = poolRepositoryPort.findById(poolToUpdate.getId())
                .orElseThrow(() -> new PoolNotFoundException("Cagnotte non trouvee avec l'id : " + poolToUpdate.getId()));

        if (poolToUpdate.getTitle() != null) pool.setTitle(poolToUpdate.getTitle());
        if (poolToUpdate.getDescription() != null) pool.setDescription(poolToUpdate.getDescription());
        if (poolToUpdate.getGoalAmount() != null) pool.setGoalAmount(poolToUpdate.getGoalAmount());
        if (poolToUpdate.getStatus() != null) pool.setStatus(poolToUpdate.getStatus());
        if (poolToUpdate.getVideoContent() != null) pool.setVideoContent(poolToUpdate.getVideoContent());
        if (poolToUpdate.getVideoContentType() != null) pool.setVideoContentType(poolToUpdate.getVideoContentType());
        if (poolToUpdate.getVideoUrl() != null) pool.setVideoUrl(poolToUpdate.getVideoUrl());

        pool.setUpdatedAt(Instant.now());

        return poolRepositoryPort.save(pool);
    }
}
