package com.alphateckplus.potify.support.infrastructure.secondary.persistence;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.data_jpa.entity.support.SupportConversationEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserStatus;
import com.alphateckplus.potify.data_jpa.repository.support.SupportConversationRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.support.application_service.secondary.SupportConversationRepositoryPort;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import com.alphateckplus.potify.support.infrastructure.secondary.persistence.mapper.SupportConversationMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupportConversationJpaAdapter implements SupportConversationRepositoryPort {

    private final SupportConversationRepository conversationRepository;
    private final UserEntityRepository userRepository;
    private final SupportConversationMapper mapper;

    @Override
    public Optional<SupportConversation> findFirstByUserIdAndStatusIn(String userIdOrEmail, List<ConversationStatus> statuses) {
        UserEntity user = getUserByEmailOrCreateGuest(userIdOrEmail);
        return conversationRepository.findFirstByUserIdAndStatusInOrderByCreatedAtDesc(user.getId(), statuses)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<SupportConversation> findById(String id) {
        return conversationRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<SupportConversation> findAllByStatusIn(List<ConversationStatus> statuses) {
        return conversationRepository.findAllByStatusIn(statuses).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<SupportConversation> findByAssignedAdminIdAndStatus(String adminEmail, ConversationStatus status, int page, int size) {
        UserEntity admin = getUserByEmailOrCreateGuest(adminEmail);
        Page<SupportConversationEntity> result = conversationRepository
                .findByAssignedAdminIdAndStatus(admin.getId(), status, PageRequest.of(page, size, Sort.by("lastMessageAt").descending()));
        return result.getContent().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public SupportConversation save(SupportConversation conversation) {
        SupportConversationEntity entity = conversationRepository.findById(conversation.getId())
                .orElseGet(() -> new SupportConversationEntity());

        if (conversation.getUserEmail() != null) {
            entity.setUser(getUserByEmailOrCreateGuest(conversation.getUserEmail()));
        }
        if (conversation.getAssignedAdminId() != null) {
            entity.setAssignedAdmin(getUserByEmailOrCreateGuest(conversation.getAssignedAdminId()));
        } else {
            entity.setAssignedAdmin(null);
        }

        entity.setStatus(conversation.getStatus());
        entity.setSubject(conversation.getSubject());
        entity.setLastMessageAt(conversation.getLastMessageAt() != null ? conversation.getLastMessageAt() : Instant.now());

        entity = conversationRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public SupportConversation createNewConversation(String userEmail) {
        UserEntity user = getUserByEmailOrCreateGuest(userEmail);
        SupportConversationEntity conversation = SupportConversationEntity.builder()
                .user(user)
                .status(ConversationStatus.BOT_ACTIVE)
                .subject("Assistance Potify")
                .lastMessageAt(Instant.now())
                .build();
        conversation = conversationRepository.save(conversation);
        return mapper.toDomain(conversation);
    }

    private UserEntity getUserByEmailOrCreateGuest(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    if (email != null && email.startsWith("guest_")) {
                        UserEntity guestUser = UserEntity.builder()
                                .fullName("Invité")
                                .email(email)
                                .password("GUEST_PASSWORD_NOT_USED")
                                .status(UserStatus.ACTIVE)
                                .enabled(true)
                                .accountNonLocked(true)
                                .build();
                        return userRepository.save(guestUser);
                    }
                    throw new IllegalArgumentException("Utilisateur non trouvé avec l'email: " + email);
                });
    }
}
