package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA des cagnottes.
 */
public interface PoolEntityRepository extends JpaRepository<PoolEntity, String> {
}
