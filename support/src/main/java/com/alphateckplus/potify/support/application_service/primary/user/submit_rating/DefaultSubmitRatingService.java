package com.alphateckplus.potify.support.application_service.primary.user.submit_rating;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.support.application_service.secondary.SupportConversationRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportRatingRepositoryPort;
import com.alphateckplus.potify.support.domain.exception.ConversationNotFoundException;
import com.alphateckplus.potify.support.domain.exception.UnauthorizedSupportAccessException;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import com.alphateckplus.potify.support.domain.model.SupportRating;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SubmitRatingRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultSubmitRatingService implements SubmitRatingUseCase {

    private final SupportConversationRepositoryPort conversationRepositoryPort;
    private final SupportRatingRepositoryPort ratingRepositoryPort;

    @Override
    @Transactional
    public void submitRating(String userEmail, String conversationId, SubmitRatingRequest request) {
        SupportConversation conversation = conversationRepositoryPort.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation non trouvée"));

        if (!conversation.getUserEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedSupportAccessException("Accès non autorisé");
        }

        SupportRating rating = SupportRating.builder()
                .conversationId(conversation.getId())
                .userId(conversation.getUserId())
                .score(request.getScore())
                .comment(request.getComment())
                .createdAt(Instant.now())
                .build();
        ratingRepositoryPort.save(rating);

        conversation.setStatus(ConversationStatus.CLOSED);
        conversationRepositoryPort.save(conversation);
    }
}
