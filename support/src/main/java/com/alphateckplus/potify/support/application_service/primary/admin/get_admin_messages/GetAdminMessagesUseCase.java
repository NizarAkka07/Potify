package com.alphateckplus.potify.support.application_service.primary.admin.get_admin_messages;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import java.util.List;

public interface GetAdminMessagesUseCase {

    List<SupportMessageDto> getConversationMessages(String conversationId, int page, int size);
}
