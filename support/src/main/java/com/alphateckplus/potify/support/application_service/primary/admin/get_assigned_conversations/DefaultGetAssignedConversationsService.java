package com.alphateckplus.potify.support.application_service.primary.admin.get_assigned_conversations;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.support.application_service.secondary.SupportConversationRepositoryPort;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultGetAssignedConversationsService implements GetAssignedConversationsUseCase {

    private final SupportConversationRepositoryPort conversationRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<SupportConversationDto> getAssignedConversationsForAdmin(String adminEmail) {
        return conversationRepositoryPort
                .findByAssignedAdminIdAndStatus(adminEmail, ConversationStatus.AGENT_ASSIGNED, 0, 100).stream()
                .map(this::mapConversationToDto)
                .toList();
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
