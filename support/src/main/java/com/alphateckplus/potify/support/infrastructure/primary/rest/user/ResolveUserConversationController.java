package com.alphateckplus.potify.support.infrastructure.primary.rest.user;

import com.alphateckplus.potify.support.application_service.primary.user.resolve_user_conversation.ResolveUserConversationUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/user")
@RequiredArgsConstructor
public class ResolveUserConversationController {

    private final ResolveUserConversationUseCase useCase;

    @PostMapping("/resolve/{conversationId}")
    public ResponseEntity<SupportConversationDto> resolveConversation(Principal principal, HttpServletRequest httpRequest, @PathVariable String conversationId) {
        String email = resolveEmail(principal, httpRequest);
        return ResponseEntity.ok(useCase.resolveConversation(email, conversationId));
    }

    private String resolveEmail(Principal principal, HttpServletRequest httpRequest) {
        if (principal != null && principal.getName() != null && !principal.getName().isBlank()) {
            return principal.getName();
        }
        String guestSession = httpRequest.getHeader("X-Guest-Session");
        if (guestSession != null && !guestSession.isBlank()) {
            return guestSession + "@potify.com";
        }
        String randomId = java.util.UUID.randomUUID().toString().substring(0, 8);
        return "guest_" + randomId + "@potify.com";
    }
}
