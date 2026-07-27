package com.alphateckplus.potify.support.infrastructure.primary.rest.user;

import com.alphateckplus.potify.support.application_service.primary.user.get_active_conversation.GetActiveConversationUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportConversationDto;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/user")
@RequiredArgsConstructor
public class GetActiveConversationController {

    private final GetActiveConversationUseCase useCase;

    @GetMapping("/active")
    public ResponseEntity<SupportConversationDto> getActiveConversation(Principal principal, HttpServletRequest httpRequest) {
        String email = resolveEmail(principal, httpRequest);
        return ResponseEntity.ok(useCase.getOrCreateActiveConversation(email));
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
