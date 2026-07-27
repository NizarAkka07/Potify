package com.alphateckplus.potify.support.application_service.secondary;

import com.alphateckplus.potify.support.domain.model.SupportMessage;
import java.util.List;

public interface SupportMessageRepositoryPort {

    List<SupportMessage> getConversationMessages(String conversationId, int page, int size);

    SupportMessage save(SupportMessage message);

    void markAllMessagesAsRead(String conversationId);
}
