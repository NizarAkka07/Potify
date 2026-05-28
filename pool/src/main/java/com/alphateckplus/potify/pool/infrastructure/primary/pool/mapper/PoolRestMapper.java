package com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper;

import com.alphateckplus.potify.pool.application_service.primary.command.CreatePoolCommand;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.CreatePoolRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PhaseResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper pour les contrats REST de la cagnotte.
 */
@Component
public class PoolRestMapper {

    public CreatePoolCommand toCommand(CreatePoolRequest request) {
        byte[] imageBytes = null;
        if (request.imageData() != null && !request.imageData().isBlank()) {
            try {
                // Supprimer le préfixe data:image/xxx;base64, si présent
                String base64Data = request.imageData();
                if (base64Data.contains(",")) {
                    base64Data = base64Data.split(",")[1];
                }
                imageBytes = java.util.Base64.getDecoder().decode(base64Data);
            } catch (IllegalArgumentException e) {
                // Log error or handle invalid base64
            }
        }

        java.util.List<com.alphateckplus.potify.pool.application_service.primary.command.PhaseCommand> phaseCmds = null;
        if (request.phases() != null) {
            phaseCmds = request.phases().stream()
                    .map(p -> new com.alphateckplus.potify.pool.application_service.primary.command.PhaseCommand(p.title(), p.goalAmount()))
                    .collect(java.util.stream.Collectors.toList());
        }

        java.util.List<com.alphateckplus.potify.pool.application_service.primary.command.SubPoolCommand> subPoolCmds = null;
        if (request.subPools() != null) {
            subPoolCmds = request.subPools().stream()
                    .map(sp -> new com.alphateckplus.potify.pool.application_service.primary.command.SubPoolCommand(
                            sp.title(),
                            sp.description(),
                            sp.hasDeadline(),
                            sp.deadlineDate(),
                            sp.phases() != null ? sp.phases().stream().map(p -> new com.alphateckplus.potify.pool.application_service.primary.command.PhaseCommand(p.title(), p.goalAmount())).collect(java.util.stream.Collectors.toList()) : null
                    ))
                    .collect(java.util.stream.Collectors.toList());
        }

        return CreatePoolCommand.builder()
                .ownerId(request.ownerId())
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .goalAmount(request.goalAmount())
                .type(request.type())
                .invitedUserIds(request.invitedUserIds())
                .imageContent(imageBytes)
                .imageContentType(request.imageContentType())
                .videoUrl(request.videoUrl())
                .parentId(request.parentId())
                .hasDeadline(request.hasDeadline())
                .deadlineDate(request.deadlineDate())
                .phases(phaseCmds)
                .subPools(subPoolCmds)
                .build();
    }

    /**
     * Mappe une entite domaine vers une reponse REST.
     */
    public PoolResponse toResponse(Pool pool) {
        if (pool == null) return null;
        
        String finalImageUrl = null;
        // Si on a du contenu en DB, on génère l'URL vers notre nouvel endpoint
        if (pool.getImageContent() != null && pool.getImageContent().length > 0) {
            finalImageUrl = "http://localhost:8082/api/pools/" + pool.getId() + "/image";
        }

        String finalVideoUrl = pool.getVideoUrl();
        if (pool.getVideoContent() != null && pool.getVideoContent().length > 0) {
            finalVideoUrl = "http://localhost:8082/api/pools/" + pool.getId() + "/video";
        }

        java.math.BigDecimal tempAmount = pool.getCurrentAmount() != null ? pool.getCurrentAmount() : java.math.BigDecimal.ZERO;
        java.util.List<PhaseResponse> phaseResponses = new java.util.ArrayList<>();
        if (pool.getPhases() != null) {
            for (com.alphateckplus.potify.pool.domain.model.Phase phase : pool.getPhases()) {
                java.math.BigDecimal phaseGoal = phase.getGoalAmount() != null ? phase.getGoalAmount() : java.math.BigDecimal.ZERO;
                java.math.BigDecimal phaseCurrent;
                if (tempAmount.compareTo(phaseGoal) >= 0) {
                    phaseCurrent = phaseGoal;
                    tempAmount = tempAmount.subtract(phaseGoal);
                } else {
                    phaseCurrent = tempAmount;
                    tempAmount = java.math.BigDecimal.ZERO;
                }
                phaseResponses.add(new PhaseResponse(
                        phase.getId(),
                        phase.getTitle(),
                        phaseGoal,
                        phaseCurrent,
                        phase.getStatus()
                ));
            }
        }

        java.util.List<PoolResponse> subPoolResponses = pool.getChildren() != null ?
                pool.getChildren().stream().map(this::toResponse).collect(java.util.stream.Collectors.toList()) :
                new java.util.ArrayList<>();

        return new PoolResponse(
                pool.getId(),
                pool.getOwnerId(),
                pool.getOwnerName(),
                pool.getTitle(),
                pool.getDescription(),
                pool.getCategory(),
                pool.getGoalAmount(),
                pool.getCurrentAmount(),
                pool.getStatus(),
                pool.getType(),
                pool.getInvitedUserIds(),
                finalImageUrl,
                finalVideoUrl,
                pool.getProgressPercentage(),
                pool.getWallet() != null ? pool.getWallet().getAvailableBalance() : java.math.BigDecimal.ZERO,
                pool.getWallet() != null ? pool.getWallet().getPendingBalance() : java.math.BigDecimal.ZERO,
                phaseResponses,
                subPoolResponses,
                pool.getHasDeadline() != null ? pool.getHasDeadline() : false,
                pool.getDeadlineDate(),
                pool.getCreatedAt(),
                pool.getUpdatedAt()
        );
    }
}
