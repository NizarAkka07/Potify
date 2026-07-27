package com.alphateckplus.potify.support.infrastructure.secondary.persistence;

import com.alphateckplus.potify.data_jpa.entity.support.MessageSenderType;
import com.alphateckplus.potify.data_jpa.entity.support.MessageStatus;
import com.alphateckplus.potify.data_jpa.entity.support.SupportConversationEntity;
import com.alphateckplus.potify.data_jpa.entity.support.SupportMessageEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.support.SupportConversationRepository;
import com.alphateckplus.potify.data_jpa.repository.support.SupportMessageRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.support.application_service.secondary.SupportMessageRepositoryPort;
import com.alphateckplus.potify.support.domain.model.SupportMessage;
import com.alphateckplus.potify.support.infrastructure.secondary.persistence.mapper.SupportMessageMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupportMessageJpaAdapter implements SupportMessageRepositoryPort {

    private final SupportMessageRepository messageRepository;
    private final SupportConversationRepository conversationRepository;
    private final UserEntityRepository userRepository;
    private final SupportMessageMapper mapper;

    @Override
    public List<SupportMessage> getConversationMessages(String conversationId, int page, int size) {
        Page<SupportMessageEntity> pageResult = messageRepository
                .findByConversationIdOrderByCreatedAtDesc(conversationId, PageRequest.of(page, size, Sort.by("createdAt").descending()));

        return pageResult.getContent().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public SupportMessage save(SupportMessage message) {
        SupportConversationEntity conversation = conversationRepository.findById(message.getConversationId())
                .orElseThrow(() -> new IllegalArgumentException("Conversation non trouvée ID: " + message.getConversationId()));

        UserEntity sender = null;
        if (message.getSenderId() != null) {
            sender = userRepository.findByEmail(message.getSenderId())
                    .orElseGet(() -> userRepository.findById(message.getSenderId()).orElse(null));
        }

        SupportMessageEntity entity = SupportMessageEntity.builder()
                .id(message.getId())
                .conversation(conversation)
                .senderType(message.getSenderType())
                .sender(sender)
                .content(message.getContent())
                .attachmentUrl(message.getAttachmentUrl())
                .attachmentType(message.getAttachmentType())
                .status(message.getStatus() != null ? message.getStatus() : MessageStatus.SENT)
                .build();

        entity = messageRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public void markAllMessagesAsRead(String conversationId) {
        messageRepository.markAllMessagesAsRead(conversationId, MessageStatus.READ);
    }
}
