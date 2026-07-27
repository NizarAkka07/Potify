package com.alphateckplus.potify.support.infrastructure.primary.rest.admin;

import com.alphateckplus.potify.support.application_service.primary.admin.transfer_conversation.TransferConversationUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import com.alphateckplus.potify.support.infrastructure.primary.dto.TransferConversationRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_AGENT') or hasAnyAuthority('ALL', 'SUPPORT_QUEUE_VIEW', 'SUPPORT_CHAT_ASSIGN')")
public class TransferConversationController {

    private final TransferConversationUseCase useCase;

    @PostMapping("/transfer/{conversationId}")
    public ResponseEntity<SupportConversationDto> transferConversation(
            Principal principal,
            @PathVariable String conversationId,
            @Valid @RequestBody TransferConversationRequest request) {
        String adminEmail = (principal != null && principal.getName() != null) ? principal.getName() : "superadmin@potify.com";
        return ResponseEntity.ok(useCase.transferConversation(adminEmail, conversationId, request));
    }
}
