package com.alphateckplus.potify.pool.infrastructure.primary.pool.dto;

import java.time.Instant;

public record PoolReportResponse(
    String id,
    String poolId,
    String userId,
    String userName,
    String reason,
    Instant createdAt
) {}
