package com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TontineMemberDto {
    private String userId;
    private String userName;
    private String userEmail;
    private Integer orderIndex;
    private Boolean hasReceivedPayout;
    private Boolean hasPaidCurrentRound;
}
