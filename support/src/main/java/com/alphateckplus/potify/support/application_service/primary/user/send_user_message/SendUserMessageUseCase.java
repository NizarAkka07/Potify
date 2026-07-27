package com.alphateckplus.potify.support.application_service.primary.user.send_user_message;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SendMessageRequest;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;

public interface SendUserMessageUseCase {

    SupportMessageDto sendMessageFromUser(String userEmail, SendMessageRequest request);
}
