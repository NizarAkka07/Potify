package com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto;

import com.alphateckplus.potify.data_jpa.entity.pool.TontineFrequency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ConfigureTontineRequest(
    @NotNull(message = "Le montant de la cotisation est obligatoire")
    @Positive(message = "Le montant de la cotisation doit être supérieur à zéro")
    BigDecimal contributionAmount,

    @NotNull(message = "La fréquence des tours est obligatoire")
    TontineFrequency frequency,

    BigDecimal latePenaltyRate
) {}
