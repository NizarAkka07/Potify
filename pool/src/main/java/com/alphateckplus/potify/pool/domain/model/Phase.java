package com.alphateckplus.potify.pool.domain.model;

import java.math.BigDecimal;
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
public class Phase {

    private String id;
    
    private String poolId;

    private String title;

    private BigDecimal goalAmount;

    private PhaseStatus status;
}
