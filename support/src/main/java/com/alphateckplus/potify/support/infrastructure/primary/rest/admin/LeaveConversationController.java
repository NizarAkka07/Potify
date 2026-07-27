package com.alphateckplus.potify.support.infrastructure.primary.rest.admin;

import com.alphateckplus.potify.support.application_service.primary.admin.leave_conversation.LeaveConversationUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_AGENT') or hasAnyAuthority('ALL', 'SUPPORT_QUEUE_VIEW', 'SUPPORT_CHAT_ASSIGN')")
public class LeaveConversationController {

    private final LeaveConversationUseCase useCase;

    @PostMapping("/leave/{conversationId}")
    public ResponseEntity<SupportConversationDto> leaveConversation(Principal principal, @PathVariable String conversationId) {
        return ResponseEntity.ok(useCase.leaveConversation(principal.getName(), conversationId));
    }
}
