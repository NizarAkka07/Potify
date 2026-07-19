package com.alphateckplus.potify.pool.application_service.secondary.pool;

import com.alphateckplus.potify.pool.domain.model.ContributorInfo;
import com.alphateckplus.potify.pool.domain.model.PoolUpdate;
import java.util.List;

/**
 * Port secondaire pour la persistance des actualités de cagnottes.
 */
public interface PoolUpdateRepositoryPort {
    
    PoolUpdate save(PoolUpdate poolUpdate);
    
    List<PoolUpdate> findByPoolId(String poolId);

    List<ContributorInfo> findContributorsByPoolId(String poolId);
}
