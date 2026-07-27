package com.alphateckplus.potify.support.application_service.primary.admin.leave_conversation;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;

public interface LeaveConversationUseCase {

    SupportConversationDto leaveConversation(String adminEmail, String conversationId);
}
