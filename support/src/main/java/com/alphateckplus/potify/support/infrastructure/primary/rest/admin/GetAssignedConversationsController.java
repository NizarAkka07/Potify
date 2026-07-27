package com.alphateckplus.potify.support.infrastructure.primary.rest.admin;

import com.alphateckplus.potify.support.application_service.primary.admin.get_assigned_conversations.GetAssignedConversationsUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import java.security.Principal;
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
public class GetAssignedConversationsController {

    private final GetAssignedConversationsUseCase useCase;

    @GetMapping("/assigned")
    public ResponseEntity<List<SupportConversationDto>> getAssignedConversations(Principal principal) {
        return ResponseEntity.ok(useCase.getAssignedConversationsForAdmin(principal.getName()));
    }
}
