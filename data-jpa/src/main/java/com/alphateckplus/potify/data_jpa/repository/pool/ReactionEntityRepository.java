package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.ReactionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA des reactions.
 */
public interface ReactionEntityRepository extends JpaRepository<ReactionEntity, String> {
    List<ReactionEntity> findByMessageId(String messageId);
}
