package com.alphateckplus.potify.pool.infrastructure.primary.pool.dto;

import com.alphateckplus.potify.pool.domain.model.PhaseStatus;
import java.math.BigDecimal;

public record PhaseResponse(
    String id,
    String title,
    BigDecimal goalAmount,
    BigDecimal currentAmount,
    PhaseStatus status
) {}
