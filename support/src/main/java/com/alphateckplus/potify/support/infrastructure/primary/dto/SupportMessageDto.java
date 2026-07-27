package com.alphateckplus.potify.support.infrastructure.primary.dto;

import com.alphateckplus.potify.data_jpa.entity.support.MessageSenderType;
import com.alphateckplus.potify.data_jpa.entity.support.MessageStatus;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportMessageDto {
    private String id;
    private String conversationId;
    private MessageSenderType senderType;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private String attachmentUrl;
    private String attachmentType;
    private MessageStatus status;
    private Instant createdAt;
}
