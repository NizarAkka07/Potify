package com.alphateckplus.potify.pool.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Message de soutien laisse sur une cagnotte.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private String poolId;
    private String userId;
    private String userName;
    private String content;
    private boolean isPublic;
    private java.util.List<Reaction> reactions;
    private java.util.List<MessageReport> reports;
    private java.time.Instant createdAt;

    public boolean isReported() {
        return reports != null && !reports.isEmpty();
    }

    public String getReportReason() {
        if (reports == null || reports.isEmpty()) return null;
        return reports.stream()
                .map(MessageReport::getReason)
                .filter(r -> r != null && !r.isBlank())
                .collect(java.util.stream.Collectors.joining(" | "));
    }

    public int getReportCount() {
        return reports != null ? reports.size() : 0;
    }
}
