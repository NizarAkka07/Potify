package com.alphateckplus.potify.pool.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Modèle de domaine pour une mise à jour / actualité de cagnotte.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoolUpdate {
    private String id;
    private String poolId;
    private String title;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
