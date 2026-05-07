package com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository;

import com.alphateckplus.potify.data_jpa.entity.pool.CommentReactionEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.MessageEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.pool.CommentReactionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.MessageEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.pool.application_service.secondary.pool.ReactionRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Reaction;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class ReactionJpaAdapter implements ReactionRepositoryPort {

    private final CommentReactionEntityRepository reactionRepository;
    private final MessageEntityRepository messageRepository;
    private final UserEntityRepository userRepository;

    @Override
    public void addOrRemoveReaction(String messageId, String userId, String type) {
        Optional<CommentReactionEntity> existing = reactionRepository.findByMessageIdAndUserIdAndReactionType(messageId, userId, type);
        
        if (existing.isPresent()) {
            reactionRepository.delete(existing.get());
        } else {
            MessageEntity message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new IllegalArgumentException("Message non trouve"));
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouve"));
            
            CommentReactionEntity entity = CommentReactionEntity.builder()
                    .message(message)
                    .user(user)
                    .reactionType(type)
                    .build();
            reactionRepository.save(entity);
        }
    }

    @Override
    public List<Reaction> findByMessageId(String messageId) {
        return reactionRepository.findByMessageId(messageId).stream()
                .map(entity -> Reaction.builder()
                        .id(entity.getId())
                        .messageId(entity.getMessage().getId())
                        .userId(entity.getUser().getId())
                        .reactionType(entity.getReactionType())
                        .build())
                .toList();
    }
}
