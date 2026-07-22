package com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository;

import com.alphateckplus.potify.data_jpa.entity.pool.CommentReactionEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.MessageEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.ReactionEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.pool.CommentReactionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.MessageEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.ReactionEntityRepository;
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
    private final ReactionEntityRepository unifiedReactionRepository;
    private final MessageEntityRepository messageRepository;
    private final UserEntityRepository userRepository;

    @Override
    public boolean toggleReaction(String targetId, String targetType, String userId) {
        UserEntity user = null;
        if (userId != null && !userId.trim().isEmpty() && !"null".equalsIgnoreCase(userId.trim())) {
            user = userRepository.findById(userId.trim()).orElse(null);
        }

        Optional<ReactionEntity> existing = Optional.empty();
        if (user != null) {
            existing = unifiedReactionRepository.findByTargetIdAndTargetTypeAndUserId(targetId, targetType, user.getId());
        }

        if (existing.isPresent()) {
            unifiedReactionRepository.delete(existing.get());
            return false;
        } else {
            ReactionEntity entity = ReactionEntity.builder()
                    .targetId(targetId)
                    .targetType(targetType)
                    .user(user)
                    .build();
            unifiedReactionRepository.save(entity);
            return true;
        }
    }

    @Override
    public List<Reaction> findByTarget(String targetId, String targetType) {
        return unifiedReactionRepository.findByTargetIdAndTargetType(targetId, targetType).stream()
                .map(entity -> Reaction.builder()
                        .id(entity.getId())
                        .targetId(entity.getTargetId())
                        .targetType(entity.getTargetType())
                        .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                        .build())
                .toList();
    }

    @Override
    public long countByTarget(String targetId, String targetType) {
        return unifiedReactionRepository.countByTargetIdAndTargetType(targetId, targetType);
    }

    @Override
    public boolean addOrRemoveReaction(String messageId, String userId, String type) {
        return toggleReaction(messageId, "MESSAGE", userId);
    }

    @Override
    public List<Reaction> findByMessageId(String messageId) {
        return findByTarget(messageId, "MESSAGE");
    }
}
