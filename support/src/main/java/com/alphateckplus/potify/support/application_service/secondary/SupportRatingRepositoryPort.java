package com.alphateckplus.potify.support.application_service.secondary;

import com.alphateckplus.potify.support.domain.model.SupportRating;

public interface SupportRatingRepositoryPort {

    SupportRating save(SupportRating rating);
}
