package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository JPA des cagnottes.
 */
public interface PoolEntityRepository extends JpaRepository<PoolEntity, String> {
    List<PoolEntity> findByType(String type);
    List<PoolEntity> findByTypeAndTitleContainingIgnoreCase(String type, String title);
}
