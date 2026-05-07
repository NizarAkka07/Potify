package com.alphateckplus.potify.pool.infrastructure.primary.pool.dto;

public record MessageRequest(
    String poolId,
    String userId,
    String content,
    boolean isPublic
) {}
