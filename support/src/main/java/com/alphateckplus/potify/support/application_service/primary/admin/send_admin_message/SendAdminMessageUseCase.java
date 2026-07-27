package com.alphateckplus.potify.support.application_service.primary.admin.send_admin_message;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SendMessageRequest;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;

public interface SendAdminMessageUseCase {

    SupportMessageDto sendMessageFromAdmin(String adminEmail, SendMessageRequest request);
}
