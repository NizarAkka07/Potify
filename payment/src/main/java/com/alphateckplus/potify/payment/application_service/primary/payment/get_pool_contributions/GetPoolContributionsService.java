package com.alphateckplus.potify.payment.application_service.primary.payment.get_pool_contributions;

import com.alphateckplus.potify.payment.domain.model.Contribution;
import java.util.List;

public interface GetPoolContributionsService {
    List<Contribution> execute(String poolId);
}
