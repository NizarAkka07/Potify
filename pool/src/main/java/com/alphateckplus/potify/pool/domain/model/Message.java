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
    private Instant createdAt;
}
