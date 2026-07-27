package com.alphateckplus.potify.support.domain.model;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
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
public class SupportConversation {

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
}
