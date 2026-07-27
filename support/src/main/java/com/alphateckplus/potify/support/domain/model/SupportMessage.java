package com.alphateckplus.potify.support.domain.model;

import com.alphateckplus.potify.data_jpa.entity.support.MessageSenderType;
import com.alphateckplus.potify.data_jpa.entity.support.MessageStatus;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportMessage {

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
