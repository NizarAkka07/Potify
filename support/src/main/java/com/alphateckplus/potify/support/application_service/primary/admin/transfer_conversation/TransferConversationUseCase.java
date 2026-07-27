package com.alphateckplus.potify.support.application_service.primary.admin.transfer_conversation;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import com.alphateckplus.potify.support.infrastructure.primary.dto.TransferConversationRequest;

public interface TransferConversationUseCase {

    SupportConversationDto transferConversation(String currentAdminEmail, String conversationId, TransferConversationRequest request);
}
