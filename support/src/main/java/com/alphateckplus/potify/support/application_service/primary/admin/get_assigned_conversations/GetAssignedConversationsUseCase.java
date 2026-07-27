package com.alphateckplus.potify.support.application_service.primary.admin.get_assigned_conversations;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import java.util.List;

public interface GetAssignedConversationsUseCase {

    List<SupportConversationDto> getAssignedConversationsForAdmin(String adminEmail);
}
