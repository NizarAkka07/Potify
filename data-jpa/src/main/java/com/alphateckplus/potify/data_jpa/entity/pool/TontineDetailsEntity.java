package com.alphateckplus.potify.data_jpa.entity.pool;

import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tontine_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TontineDetailsEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id", nullable = false, unique = true)
    private PoolEntity pool;

    @Column(name = "contribution_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal contributionAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 30)
    private TontineFrequency frequency;

    @Column(name = "late_penalty_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal latePenaltyRate = BigDecimal.ZERO; // Pénalité en % par jour/tour de retard

    @Column(name = "total_rounds")
    @Builder.Default
    private Integer totalRounds = 0;

    @Column(name = "current_round_number")
    @Builder.Default
    private Integer currentRoundNumber = 1;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private TontineStatus status = TontineStatus.DRAFT;
}
