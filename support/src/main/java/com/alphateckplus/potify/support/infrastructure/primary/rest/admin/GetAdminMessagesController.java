package com.alphateckplus.potify.support.infrastructure.primary.rest.admin;

import com.alphateckplus.potify.support.application_service.primary.admin.get_admin_messages.GetAdminMessagesUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_AGENT') or hasAnyAuthority('ALL', 'SUPPORT_QUEUE_VIEW', 'SUPPORT_CHAT_ASSIGN')")
public class GetAdminMessagesController {

    private final GetAdminMessagesUseCase useCase;

    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<List<SupportMessageDto>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return ResponseEntity.ok(useCase.getConversationMessages(conversationId, page, size));
    }
}
