package com.alphateckplus.potify.notification.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String id;
    private String userId;
    private String type; // e.g. "MESSAGE", "CONTRIBUTION", "REACTION"
    private String channel; // e.g. "EMAIL", "SMS", "NOTIF_APP"
    private String title;
    private String content;
    private String status; // e.g. "ACTIVE", "COMPLETED", etc.
    private Instant createdAt;
}
