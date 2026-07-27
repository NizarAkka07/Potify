package com.alphateckplus.potify.support.domain.model;

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
public class SupportRating {

    private String id;
    private String conversationId;
    private String userId;
    private int score;
    private String comment;
    private Instant createdAt;
}
