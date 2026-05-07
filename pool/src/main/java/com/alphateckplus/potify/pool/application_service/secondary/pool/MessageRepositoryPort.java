package com.alphateckplus.potify.pool.application_service.secondary.pool;

import com.alphateckplus.potify.pool.domain.model.Message;
import java.util.List;

public interface MessageRepositoryPort {
    Message save(Message message);
    List<Message> findByPoolId(String poolId);
    void deleteById(String id);
}
