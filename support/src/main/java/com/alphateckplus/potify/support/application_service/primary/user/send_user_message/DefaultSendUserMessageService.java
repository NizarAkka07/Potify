package com.alphateckplus.potify.support.application_service.primary.user.send_user_message;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.data_jpa.entity.support.MessageSenderType;
import com.alphateckplus.potify.data_jpa.entity.support.MessageStatus;
import com.alphateckplus.potify.support.application_service.secondary.SupportAiEnginePort;
import com.alphateckplus.potify.support.application_service.secondary.SupportConversationRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportMessageRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportNotificationPort;
import com.alphateckplus.potify.support.domain.exception.ConversationNotFoundException;
import com.alphateckplus.potify.support.domain.exception.UnauthorizedSupportAccessException;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import com.alphateckplus.potify.support.domain.model.SupportMessage;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SendMessageRequest;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import com.alphateckplus.potify.support.infrastructure.secondary.ai.PotifyAiSupportEngine.AiSupportResponse;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultSendUserMessageService implements SendUserMessageUseCase {

    private final SupportConversationRepositoryPort conversationRepositoryPort;
    private final SupportMessageRepositoryPort messageRepositoryPort;
    private final SupportAiEnginePort aiEnginePort;
    private final SupportNotificationPort notificationPort;

    @Override
    @Transactional
    public SupportMessageDto sendMessageFromUser(String userEmail, SendMessageRequest request) {
        SupportConversation conversation = conversationRepositoryPort.findById(request.getConversationId())
                .orElseThrow(() -> new ConversationNotFoundException("Conversation non trouvée"));

        if (!conversation.getUserEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedSupportAccessException("Vous n'êtes pas autorisé à envoyer un message dans cette conversation");
        }

        SupportMessage userMsg = SupportMessage.builder()
                .conversationId(conversation.getId())
                .senderType(MessageSenderType.USER)
                .senderId(conversation.getUserId())
                .senderName(conversation.getUserName())
                .senderAvatar(conversation.getUserAvatar())
                .content(request.getContent())
                .status(MessageStatus.SENT)
                .createdAt(Instant.now())
                .build();
        userMsg = messageRepositoryPort.save(userMsg);

        conversation.setLastMessageAt(Instant.now());
        conversationRepositoryPort.save(conversation);

        SupportMessageDto userMsgDto = mapMessageToDto(userMsg);
        notificationPort.broadcastToConversation(conversation.getId(), userMsgDto);

        if (ConversationStatus.BOT_ACTIVE.equals(conversation.getStatus())) {
            AiSupportResponse aiResponse = aiEnginePort.generateReply(request.getContent());

            SupportMessage botMsg = SupportMessage.builder()
                    .conversationId(conversation.getId())
                    .senderType(MessageSenderType.BOT)
                    .senderName("Chatbot Potify")
                    .content(aiResponse.getReplyText())
                    .status(MessageStatus.SENT)
                    .createdAt(Instant.now())
                    .build();
            botMsg = messageRepositoryPort.save(botMsg);

            SupportMessageDto botMsgDto = mapMessageToDto(botMsg);
            notificationPort.broadcastToConversation(conversation.getId(), botMsgDto);

            if (aiResponse.isShouldEscalate()) {
                escalateConversationInternal(conversation, "Escalade automatique par le Bot IA");
            }
        } else {
            notificationPort.notifyAdminQueueUpdate();
        }

        return userMsgDto;
    }

    private void escalateConversationInternal(SupportConversation conversation, String reason) {
        conversation.setStatus(ConversationStatus.PENDING_AGENT);
        conversationRepositoryPort.save(conversation);

        SupportMessage sysMsg = SupportMessage.builder()
                .conversationId(conversation.getId())
                .senderType(MessageSenderType.SYSTEM)
                .content("🔔 Demande d'assistance transmise à la file d'attente des administrateurs. (" + reason + ")")
                .status(MessageStatus.SENT)
                .createdAt(Instant.now())
                .build();
        sysMsg = messageRepositoryPort.save(sysMsg);

        notificationPort.broadcastToConversation(conversation.getId(), mapMessageToDto(sysMsg));
        notificationPort.notifyAdminQueueUpdate();
    }

    private SupportMessageDto mapMessageToDto(SupportMessage model) {
        return SupportMessageDto.builder()
                .id(model.getId())
                .conversationId(model.getConversationId())
                .senderType(model.getSenderType())
                .senderId(model.getSenderId())
                .senderName(model.getSenderName())
                .senderAvatar(model.getSenderAvatar())
                .content(model.getContent())
                .attachmentUrl(model.getAttachmentUrl())
                .attachmentType(model.getAttachmentType())
                .status(model.getStatus())
                .createdAt(model.getCreatedAt())
                .build();
    }
}
