package com.alphateckplus.potify.support.application_service.primary.admin.resolve_admin_conversation;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;

public interface ResolveAdminConversationUseCase {

    SupportConversationDto resolveConversation(String adminEmail, String conversationId);
}
