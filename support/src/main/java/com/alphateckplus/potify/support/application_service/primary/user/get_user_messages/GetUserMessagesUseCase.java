package com.alphateckplus.potify.support.application_service.primary.user.get_user_messages;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import java.util.List;

public interface GetUserMessagesUseCase {

    List<SupportMessageDto> getConversationMessages(String conversationId, int page, int size);
}
