package com.alphateckplus.potify.support.application_service.primary.user.submit_rating;

import com.alphateckplus.potify.support.infrastructure.primary.dto.SubmitRatingRequest;

public interface SubmitRatingUseCase {

    void submitRating(String userEmail, String conversationId, SubmitRatingRequest request);
}
