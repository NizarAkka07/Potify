package com.alphateckplus.potify.pool.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoolReport {
    private String id;
    private String poolId;
    private String userId;
    private String userName;
    private String reason;
    private Instant createdAt;
}
