package com.alphateckplus.potify.pool.application_service.primary.command;

import java.math.BigDecimal;

public record PhaseCommand(
    String title,
    BigDecimal goalAmount
) {}
