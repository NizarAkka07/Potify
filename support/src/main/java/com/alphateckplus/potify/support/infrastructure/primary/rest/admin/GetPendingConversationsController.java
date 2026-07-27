package com.alphateckplus.potify.support.infrastructure.primary.rest.admin;

import com.alphateckplus.potify.support.application_service.primary.admin.get_pending_conversations.GetPendingConversationsUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_AGENT') or hasAnyAuthority('ALL', 'SUPPORT_QUEUE_VIEW', 'SUPPORT_CHAT_ASSIGN')")
public class GetPendingConversationsController {

    private final GetPendingConversationsUseCase useCase;

    @GetMapping("/pending")
    public ResponseEntity<List<SupportConversationDto>> getPendingConversations() {
        return ResponseEntity.ok(useCase.getPendingConversations());
    }
}
