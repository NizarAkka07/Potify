package com.alphateckplus.potify.support.infrastructure.primary.dto;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportConversationDto {
    private String id;
    private String userId;
    private String userName;
    private String userEmail;
    private String userAvatar;
    private String assignedAdminId;
    private String assignedAdminName;
    private ConversationStatus status;
    private String subject;
    private Instant lastMessageAt;
    private Instant createdAt;
    private long unreadCount;
    private SupportMessageDto lastMessage;
}
