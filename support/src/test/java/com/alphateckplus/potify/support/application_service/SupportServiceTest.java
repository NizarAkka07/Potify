package com.alphateckplus.potify.support.application_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.data_jpa.entity.support.MessageSenderType;
import com.alphateckplus.potify.data_jpa.entity.support.MessageStatus;
import com.alphateckplus.potify.support.application_service.primary.admin.assign_conversation.DefaultAssignConversationService;
import com.alphateckplus.potify.support.application_service.primary.user.get_active_conversation.DefaultGetActiveConversationService;
import com.alphateckplus.potify.support.application_service.primary.user.send_user_message.DefaultSendUserMessageService;
import com.alphateckplus.potify.support.application_service.secondary.SupportAiEnginePort;
import com.alphateckplus.potify.support.application_service.secondary.SupportConversationRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportMessageRepositoryPort;
import com.alphateckplus.potify.support.application_service.secondary.SupportNotificationPort;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import com.alphateckplus.potify.support.domain.model.SupportMessage;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SendMessageRequest;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import com.alphateckplus.potify.support.infrastructure.secondary.ai.PotifyAiSupportEngine;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SupportServiceTest {

    private SupportConversationRepositoryPort conversationRepositoryPort;
    private SupportMessageRepositoryPort messageRepositoryPort;
    private SupportAiEnginePort aiEnginePort;
    private SupportNotificationPort notificationPort;

    private DefaultGetActiveConversationService getActiveConversationService;
    private DefaultSendUserMessageService sendUserMessageService;
    private DefaultAssignConversationService assignConversationService;

    private SupportConversation mockConversation;

    @BeforeEach
    void setUp() {
        conversationRepositoryPort = Mockito.mock(SupportConversationRepositoryPort.class);
        messageRepositoryPort = Mockito.mock(SupportMessageRepositoryPort.class);
        aiEnginePort = new PotifyAiSupportEngine();
        notificationPort = Mockito.mock(SupportNotificationPort.class);

        getActiveConversationService = new DefaultGetActiveConversationService(
                conversationRepositoryPort,
                messageRepositoryPort
        );

        sendUserMessageService = new DefaultSendUserMessageService(
                conversationRepositoryPort,
                messageRepositoryPort,
                aiEnginePort,
                notificationPort
        );

        assignConversationService = new DefaultAssignConversationService(
                conversationRepositoryPort,
                messageRepositoryPort,
                notificationPort
        );

        mockConversation = SupportConversation.builder()
                .id("conv-789")
                .userId("user-123")
                .userName("John Doe")
                .userEmail("user@potify.com")
                .status(ConversationStatus.BOT_ACTIVE)
                .subject("Assistance Potify")
                .createdAt(Instant.now())
                .lastMessageAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Devrait créer une nouvelle conversation pour l'utilisateur")
    void testGetOrCreateActiveConversation() {
        Mockito.when(conversationRepositoryPort.findFirstByUserIdAndStatusIn(eq("user@potify.com"), any()))
                .thenReturn(Optional.empty());
        Mockito.when(conversationRepositoryPort.createNewConversation("user@potify.com"))
                .thenReturn(mockConversation);

        SupportConversationDto dto = getActiveConversationService.getOrCreateActiveConversation("user@potify.com");

        assertNotNull(dto);
        assertEquals("conv-789", dto.getId());
        assertEquals("John Doe", dto.getUserName());
        assertEquals(ConversationStatus.BOT_ACTIVE, dto.getStatus());
    }

    @Test
    @DisplayName("Devrait traiter un message utilisateur et générer la réponse IA du bot")
    void testSendMessageFromUserWithAiReply() {
        Mockito.when(conversationRepositoryPort.findById("conv-789")).thenReturn(Optional.of(mockConversation));

        SupportMessage userMsg = SupportMessage.builder()
                .id("msg-1")
                .conversationId("conv-789")
                .senderType(MessageSenderType.USER)
                .senderName("John Doe")
                .content("Comment créer une cagnotte ?")
                .status(MessageStatus.SENT)
                .createdAt(Instant.now())
                .build();

        Mockito.when(messageRepositoryPort.save(any())).thenReturn(userMsg);

        SendMessageRequest request = SendMessageRequest.builder()
                .conversationId("conv-789")
                .content("Comment créer une cagnotte ?")
                .build();

        SupportMessageDto dto = sendUserMessageService.sendMessageFromUser("user@potify.com", request);

        assertNotNull(dto);
        assertEquals(MessageSenderType.USER, dto.getSenderType());
        Mockito.verify(messageRepositoryPort, Mockito.atLeast(2)).save(any());
    }

    @Test
    @DisplayName("Devrait assigner la conversation à un administrateur")
    void testAssignConversationToAdmin() {
        Mockito.when(conversationRepositoryPort.findById("conv-789")).thenReturn(Optional.of(mockConversation));
        Mockito.when(conversationRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(messageRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SupportConversationDto dto = assignConversationService.assignConversationToAdmin("supportagent@potify.com", "conv-789");

        assertNotNull(dto);
        assertEquals(ConversationStatus.AGENT_ASSIGNED, dto.getStatus());
        assertEquals("supportagent@potify.com", dto.getAssignedAdminName());
    }
}
