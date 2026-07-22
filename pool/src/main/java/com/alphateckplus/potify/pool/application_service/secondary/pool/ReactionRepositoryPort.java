package com.alphateckplus.potify.pool.application_service.secondary.pool;

import com.alphateckplus.potify.pool.domain.model.Reaction;
import java.util.List;

public interface ReactionRepositoryPort {
    boolean toggleReaction(String targetId, String targetType, String userId);
    List<Reaction> findByTarget(String targetId, String targetType);
    long countByTarget(String targetId, String targetType);
    boolean addOrRemoveReaction(String messageId, String userId, String type);
    List<Reaction> findByMessageId(String messageId);
}
