package com.alphateckplus.potify.support.application_service.secondary;

import com.alphateckplus.potify.support.infrastructure.secondary.ai.PotifyAiSupportEngine.AiSupportResponse;

public interface SupportAiEnginePort {

    AiSupportResponse generateReply(String userMessageContent);
}
