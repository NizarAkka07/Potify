package com.alphateckplus.potify.pool.domain.model;

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
public class Reaction {
    private String id;
    private String messageId;
    private String userId;
    private String reactionType; // "LIKE", "HEART", etc.
}
