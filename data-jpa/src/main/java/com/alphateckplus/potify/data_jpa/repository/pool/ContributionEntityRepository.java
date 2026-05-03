package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.ContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository JPA pour les contributions.
 */
public interface ContributionEntityRepository extends JpaRepository<ContributionEntity, String> {
    List<ContributionEntity> findByPoolId(String poolId);
    List<ContributionEntity> findByUserId(String userId);
}
