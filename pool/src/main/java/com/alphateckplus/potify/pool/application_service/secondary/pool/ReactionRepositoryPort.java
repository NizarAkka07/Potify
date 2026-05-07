package com.alphateckplus.potify.pool.application_service.secondary.pool;

import com.alphateckplus.potify.pool.domain.model.Reaction;
import java.util.List;

public interface ReactionRepositoryPort {
    void addOrRemoveReaction(String messageId, String userId, String type);
    List<Reaction> findByMessageId(String messageId);
}
