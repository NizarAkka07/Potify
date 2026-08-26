package com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto;

import com.alphateckplus.potify.data_jpa.entity.pool.TontineFrequency;
import com.alphateckplus.potify.data_jpa.entity.pool.TontineStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TontineSummaryResponse {
    private String poolId;
    private String poolTitle;
    private BigDecimal contributionAmount;
    private TontineFrequency frequency;
    private BigDecimal latePenaltyRate;
    private Integer totalRounds;
    private Integer currentRoundNumber;
    private TontineStatus status;
    private LocalDateTime startDate;
    private List<TontineMemberDto> members;
    private TontineRoundDto activeRound;
    private List<TontineRoundDto> allRounds;
    private Boolean invitationsLocked;
    private Integer resetCount;
}
