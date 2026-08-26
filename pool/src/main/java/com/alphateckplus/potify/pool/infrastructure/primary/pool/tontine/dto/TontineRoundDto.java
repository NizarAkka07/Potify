package com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto;

import com.alphateckplus.potify.data_jpa.entity.pool.TontineRoundStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TontineRoundDto {
    private String id;
    private Integer roundNumber;
    private String beneficiaryId;
    private String beneficiaryName;
    private LocalDateTime dueDate;
    private BigDecimal targetAmount;
    private BigDecimal collectedAmount;
    private BigDecimal collectedPenalties;
    private TontineRoundStatus status;
    private LocalDateTime payoutDate;
    private String payoutTransactionId;
}
