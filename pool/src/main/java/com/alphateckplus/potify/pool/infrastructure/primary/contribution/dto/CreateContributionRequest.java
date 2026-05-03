package com.alphateckplus.potify.pool.infrastructure.primary.contribution.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Contrat d'entree pour une contribution.
 */
public record CreateContributionRequest(
    @NotBlank(message = "L'ID de la cagnotte est obligatoire")
    String poolId,

    String userId,

    @Email(message = "Format d'email invalide")
    String contributorEmail,

    String contributorName,

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    BigDecimal amount,

    String message,

    boolean anonymous,

    @NotBlank(message = "Le moyen de paiement est obligatoire")
    String paymentMethod
) {}
