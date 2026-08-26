package com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto;

import jakarta.validation.constraints.NotBlank;

public record TontineContributeRequest(
    @NotBlank(message = "L'identifiant de l'utilisateur est obligatoire")
    String userId,

    String paymentMethod
) {}
