package com.alphateckplus.potify.support.application_service.primary.admin.get_pending_conversations;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import java.util.List;

public interface GetPendingConversationsUseCase {

    List<SupportConversationDto> getPendingConversations();
}
