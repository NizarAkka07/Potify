package com.alphateckplus.potify.support.application_service.primary.admin.get_admin_messages;

import com.alphateckplus.potify.support.application_service.secondary.SupportMessageRepositoryPort;
import com.alphateckplus.potify.support.domain.model.SupportMessage;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultGetAdminMessagesService implements GetAdminMessagesUseCase {

    private final SupportMessageRepositoryPort messageRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<SupportMessageDto> getConversationMessages(String conversationId, int page, int size) {
        return messageRepositoryPort.getConversationMessages(conversationId, page, size).stream()
                .map(this::mapMessageToDto)
                .toList();
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
