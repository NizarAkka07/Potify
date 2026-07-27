package com.alphateckplus.potify.support.application_service.primary.user.resolve_user_conversation;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;

public interface ResolveUserConversationUseCase {

    SupportConversationDto resolveConversation(String userEmail, String conversationId);
}
