package com.alphateckplus.potify.pool.infrastructure.primary.pool.dto;

import java.math.BigDecimal;

public record PhaseRequest(
    String title,
    BigDecimal goalAmount
) {}
