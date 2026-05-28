package com.alphateckplus.potify.pool.infrastructure.primary.pool.dto;

import com.alphateckplus.potify.pool.domain.model.PoolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrat d'entree pour la creation d'une cagnotte.
 */
public record CreatePoolRequest(
    @NotBlank(message = "L'identifiant du proprietaire est obligatoire")
    String ownerId,

    @NotBlank(message = "Le titre est obligatoire")
    String title,

    @NotBlank(message = "La description est obligatoire")
    String description,

    String category,

    @NotNull(message = "Le montant cible est obligatoire")
    @Positive(message = "Le montant cible doit être positif")
    BigDecimal goalAmount,

    @NotNull(message = "Le type est obligatoire")
    PoolType type,

    Set<String> invitedUserIds,
    String imageData, // Base64 string
    String imageContentType,
    String videoUrl,
    String parentId,
    Boolean hasDeadline,
    LocalDateTime deadlineDate,
    List<PhaseRequest> phases,
    List<SubPoolRequest> subPools
) {}
