package com.alphateckplus.potify.pool.infrastructure.primary.pool.dto;

import java.time.Instant;

/**
 * DTO de réponse représentant une actualité / mise à jour de cagnotte.
 */
public record PoolUpdateResponse(
    String id,
    String poolId,
    String title,
    String content,
    String imageUrl,
    String videoUrl,
    Instant createdAt,
    Instant updatedAt,
    long likesCount,
    boolean userLiked
) {
    public PoolUpdateResponse(String id, String poolId, String title, String content, String imageUrl, String videoUrl, Instant createdAt, Instant updatedAt) {
        this(id, poolId, title, content, imageUrl, videoUrl, createdAt, updatedAt, 0L, false);
    }
}
