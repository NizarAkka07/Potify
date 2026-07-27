package com.alphateckplus.potify.support.infrastructure.primary.rest.admin;

import com.alphateckplus.potify.support.application_service.primary.admin.send_admin_message.SendAdminMessageUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SendMessageRequest;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_AGENT') or hasAnyAuthority('ALL', 'SUPPORT_QUEUE_VIEW', 'SUPPORT_CHAT_ASSIGN')")
public class SendAdminMessageController {

    private final SendAdminMessageUseCase useCase;

    @PostMapping("/send")
    public ResponseEntity<SupportMessageDto> sendMessage(Principal principal, @Valid @RequestBody SendMessageRequest request) {
        String adminEmail = (principal != null && principal.getName() != null) ? principal.getName() : "superadmin@potify.com";
        return ResponseEntity.ok(useCase.sendMessageFromAdmin(adminEmail, request));
    }
}
