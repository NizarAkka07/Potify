package com.alphateckplus.potify.pool.infrastructure.primary;

import com.alphateckplus.potify.pool.application_service.primary.command.CreatePoolCommand;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.infrastructure.primary.dto.CreatePoolRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.dto.PoolResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper pour les contrats REST.
 */
@Component
public class PoolRestMapper {

    /**
     * Mappe une requete de creation vers une commande applicative.
     */
    public CreatePoolCommand toCommand(CreatePoolRequest request) {
        return CreatePoolCommand.builder()
                .ownerId(request.ownerId())
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .goalAmount(request.goalAmount())
                .type(request.type())
                .invitedUserIds(request.invitedUserIds())
                .build();
    }

    /**
     * Mappe une entite domaine vers une reponse REST.
     */
    public PoolResponse toResponse(Pool pool) {
        return new PoolResponse(
                pool.getId(),
                pool.getOwnerId(),
                pool.getTitle(),
                pool.getDescription(),
                pool.getCategory(),
                pool.getGoalAmount(),
                pool.getCurrentAmount(),
                pool.getStatus(),
                pool.getType(),
                pool.getInvitedUserIds(),
                pool.getProgressPercentage(),
                pool.getCreatedAt(),
                pool.getUpdatedAt()
        );
    }
}
