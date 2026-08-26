package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.TontineMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TontineMemberEntityRepository extends JpaRepository<TontineMemberEntity, String> {
    List<TontineMemberEntity> findByPoolIdOrderByOrderIndexAsc(String poolId);
    Optional<TontineMemberEntity> findByPoolIdAndUserId(String poolId, String userId);
    Optional<TontineMemberEntity> findByPoolIdAndOrderIndex(String poolId, Integer orderIndex);
    boolean existsByPoolIdAndUserId(String poolId, String userId);
    long countByPoolId(String poolId);
}
