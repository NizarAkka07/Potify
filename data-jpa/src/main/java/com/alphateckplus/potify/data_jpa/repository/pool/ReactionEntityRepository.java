package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.ReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionEntityRepository extends JpaRepository<ReactionEntity, String> {

    List<ReactionEntity> findByTargetIdAndTargetType(String targetId, String targetType);

    Optional<ReactionEntity> findByTargetIdAndTargetTypeAndUserId(
            String targetId, String targetType, String userId
    );

    long countByTargetIdAndTargetType(String targetId, String targetType);

    boolean existsByTargetIdAndTargetTypeAndUserId(
            String targetId, String targetType, String userId
    );
}
