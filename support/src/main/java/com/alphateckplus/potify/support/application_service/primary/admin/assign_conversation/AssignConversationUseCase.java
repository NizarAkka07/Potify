package com.alphateckplus.potify.support.application_service.primary.admin.assign_conversation;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;

public interface AssignConversationUseCase {

    SupportConversationDto assignConversationToAdmin(String adminEmail, String conversationId);
}
