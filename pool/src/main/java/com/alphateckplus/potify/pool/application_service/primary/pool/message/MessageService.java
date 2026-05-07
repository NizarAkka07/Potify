package com.alphateckplus.potify.pool.application_service.primary.pool.message;

import com.alphateckplus.potify.pool.domain.model.Message;
import java.util.List;

public interface MessageService {
    Message addMessage(Message message);
    List<Message> getPoolMessages(String poolId);
}
