package com.alphateckplus.potify.pool.application_service.primary.pool.message;

import com.alphateckplus.potify.pool.application_service.secondary.pool.MessageRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultMessageService implements MessageService {

    private final MessageRepositoryPort messageRepositoryPort;

    @Override
    public Message addMessage(Message message) {
        return messageRepositoryPort.save(message);
    }

    @Override
    public List<Message> getPoolMessages(String poolId) {
        return messageRepositoryPort.findByPoolId(poolId);
    }
}
