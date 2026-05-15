package com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper;

import com.alphateckplus.potify.pool.application_service.primary.command.CreatePoolCommand;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.CreatePoolRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolResponse;
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
                pool.getVideoUrl(),
                pool.getProgressPercentage(),
                pool.getWallet() != null ? pool.getWallet().getAvailableBalance() : java.math.BigDecimal.ZERO,
                pool.getWallet() != null ? pool.getWallet().getPendingBalance() : java.math.BigDecimal.ZERO,
                pool.getCreatedAt(),
                pool.getUpdatedAt()
        );
    }
}
