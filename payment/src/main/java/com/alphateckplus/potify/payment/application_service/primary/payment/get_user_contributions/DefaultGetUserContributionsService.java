package com.alphateckplus.potify.payment.application_service.primary.payment.get_user_contributions;

import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.domain.model.Contribution;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class DefaultGetUserContributionsService implements GetUserContributionsService {

    private final PaymentRepositoryPort repositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<Contribution> execute(String userId) {
        return repositoryPort.findContributionsByUserId(userId);
    }
}
