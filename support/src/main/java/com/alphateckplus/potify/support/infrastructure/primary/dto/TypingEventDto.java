package com.alphateckplus.potify.support.infrastructure.primary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingEventDto {
    private String conversationId;
    private String userId;
    private String userName;
    private boolean typing;
}
