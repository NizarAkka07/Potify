package com.alphateckplus.potify.support.infrastructure.secondary.persistence;

import com.alphateckplus.potify.data_jpa.entity.support.SupportConversationEntity;
import com.alphateckplus.potify.data_jpa.entity.support.SupportRatingEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.support.SupportConversationRepository;
import com.alphateckplus.potify.data_jpa.repository.support.SupportRatingRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.support.application_service.secondary.SupportRatingRepositoryPort;
import com.alphateckplus.potify.support.domain.model.SupportRating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupportRatingJpaAdapter implements SupportRatingRepositoryPort {

    private final SupportRatingRepository ratingRepository;
    private final SupportConversationRepository conversationRepository;
    private final UserEntityRepository userRepository;

    @Override
    public SupportRating save(SupportRating rating) {
        SupportConversationEntity conversation = conversationRepository.findById(rating.getConversationId())
                .orElseThrow(() -> new IllegalArgumentException("Conversation non trouvée ID: " + rating.getConversationId()));

        UserEntity user = userRepository.findById(rating.getUserId())
                .orElseGet(() -> userRepository.findByEmail(rating.getUserId()).orElse(conversation.getUser()));

        SupportRatingEntity entity = SupportRatingEntity.builder()
                .conversation(conversation)
                .user(user)
                .score(rating.getScore())
                .comment(rating.getComment())
                .build();

        entity = ratingRepository.save(entity);
        return SupportRating.builder()
                .id(entity.getId())
                .conversationId(entity.getConversation().getId())
                .userId(entity.getUser().getId())
                .score(entity.getScore())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
