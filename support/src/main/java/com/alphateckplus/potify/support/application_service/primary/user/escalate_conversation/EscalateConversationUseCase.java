package com.alphateckplus.potify.support.application_service.primary.user.escalate_conversation;

public interface EscalateConversationUseCase {

    void escalateConversation(String userEmail, String conversationId);
}
