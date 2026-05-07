package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.CommentReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommentReactionEntityRepository extends JpaRepository<CommentReactionEntity, String> {
    List<CommentReactionEntity> findByMessageId(String messageId);
    Optional<CommentReactionEntity> findByMessageIdAndUserIdAndReactionType(String messageId, String userId, String reactionType);
    void deleteByMessageIdAndUserIdAndReactionType(String messageId, String userId, String reactionType);
}
