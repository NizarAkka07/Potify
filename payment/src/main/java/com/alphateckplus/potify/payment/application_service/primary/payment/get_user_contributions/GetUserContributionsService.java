package com.alphateckplus.potify.payment.application_service.primary.payment.get_user_contributions;

import com.alphateckplus.potify.payment.domain.model.Contribution;
import java.util.List;

public interface GetUserContributionsService {
    List<Contribution> execute(String userId);
}
