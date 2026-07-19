package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolUpdateEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour les actualités et mises à jour de cagnottes.
 */
@Repository
public interface PoolUpdateEntityRepository extends JpaRepository<PoolUpdateEntity, String> {
    
    List<PoolUpdateEntity> findByPoolIdOrderByCreatedAtDesc(String poolId);
}
