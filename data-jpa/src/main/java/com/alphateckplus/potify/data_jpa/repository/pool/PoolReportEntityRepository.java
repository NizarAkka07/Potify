package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PoolReportEntityRepository extends JpaRepository<PoolReportEntity, String> {
    Optional<PoolReportEntity> findByPoolIdAndUserId(String poolId, String userId);
    void deleteByPoolId(String poolId);
}
