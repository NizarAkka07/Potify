package com.alphateckplus.potify.pool.infrastructure.primary.contribution.mapper;

import com.alphateckplus.potify.pool.application_service.primary.command.CreateContributionCommand;
import com.alphateckplus.potify.pool.domain.model.Contribution;
import com.alphateckplus.potify.pool.infrastructure.primary.contribution.dto.CreateContributionRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.contribution.dto.ContributionResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper pour les contrats REST des contributions.
 */
@Component
public class ContributionRestMapper {

    public CreateContributionCommand toCommand(CreateContributionRequest request) {
        return CreateContributionCommand.builder()
                .poolId(request.poolId())
                .userId(request.userId())
                .contributorEmail(request.contributorEmail())
                .contributorName(request.contributorName())
                .amount(request.amount())
                .message(request.message())
                .anonymous(request.anonymous())
                .paymentMethod(request.paymentMethod())
                .build();
    }

    public ContributionResponse toResponse(Contribution contribution) {
        if (contribution == null) return null;

        return new ContributionResponse(
                contribution.getId(),
                contribution.getPoolId(),
                contribution.getUserId(),
                contribution.getContributorEmail(),
                contribution.getContributorName(),
                contribution.getAmount(),
                contribution.getMessage(),
                contribution.isAnonymous(),
                contribution.getStatus(),
                contribution.getPaymentMethod(),
                contribution.getCreatedAt()
        );
    }
}
