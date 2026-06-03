package com.alphateckplus.potify.payment.application_service.primary.payment.get_pool_contributions;

import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.domain.model.Contribution;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class DefaultGetPoolContributionsService implements GetPoolContributionsService {

    private final PaymentRepositoryPort repositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<Contribution> execute(String poolId) {
        return repositoryPort.findContributionsByPoolId(poolId);
    }
}
