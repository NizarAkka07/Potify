package com.alphateckplus.potify.support.application_service.primary.user.get_active_conversation;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.data_jpa.entity.support.MessageSenderType;
import com.alphateckplus.potify.data_jpa.entity.support.MessageStatus;
import com.alphateckplus.potify.support.application_service.secondary.SupportConversationRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportMessageRepositoryPort;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import com.alphateckplus.potify.support.domain.model.SupportMessage;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultGetActiveConversationService implements GetActiveConversationUseCase {

    private final SupportConversationRepositoryPort conversationRepositoryPort;
    private final SupportMessageRepositoryPort messageRepositoryPort;

    @Override
    @Transactional
    public SupportConversationDto getOrCreateActiveConversation(String userEmail) {
        List<ConversationStatus> activeStatuses = List.of(
                ConversationStatus.BOT_ACTIVE,
                ConversationStatus.PENDING_AGENT,
                ConversationStatus.AGENT_ASSIGNED
        );

        Optional<SupportConversation> existing = conversationRepositoryPort
                .findFirstByUserIdAndStatusIn(userEmail, activeStatuses);

        if (existing.isPresent()) {
            return mapConversationToDto(existing.get());
        }

        SupportConversation conversation = conversationRepositoryPort.createNewConversation(userEmail);

        SupportMessage welcomeMsg = SupportMessage.builder()
                .conversationId(conversation.getId())
                .senderType(MessageSenderType.BOT)
                .senderName("Chatbot Potify")
                .content("Bonjour " + conversation.getUserName() + " ! 🤖 Je suis l'assistant virtuel Potify. Comment puis-je vous aider aujourd'hui ? (Cagnottes, dons, retraits, vérification KYC, comptes)")
                .status(MessageStatus.SENT)
                .createdAt(Instant.now())
                .build();
        messageRepositoryPort.save(welcomeMsg);

        return mapConversationToDto(conversation);
    }

    private SupportConversationDto mapConversationToDto(SupportConversation model) {
        return SupportConversationDto.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .userName(model.getUserName())
                .userEmail(model.getUserEmail())
                .userAvatar(model.getUserAvatar())
                .assignedAdminId(model.getAssignedAdminId())
                .assignedAdminName(model.getAssignedAdminName())
                .status(model.getStatus())
                .subject(model.getSubject())
                .lastMessageAt(model.getLastMessageAt())
                .createdAt(model.getCreatedAt())
                .build();
    }
}
