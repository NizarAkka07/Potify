package com.alphateckplus.potify.support.application_service.primary.user.get_active_conversation;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;

public interface GetActiveConversationUseCase {

    SupportConversationDto getOrCreateActiveConversation(String userEmail);
}
