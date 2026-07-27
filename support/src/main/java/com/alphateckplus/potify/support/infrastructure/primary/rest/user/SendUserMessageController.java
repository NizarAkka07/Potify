package com.alphateckplus.potify.support.infrastructure.primary.rest.user;

import com.alphateckplus.potify.support.application_service.primary.user.send_user_message.SendUserMessageUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SendMessageRequest;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/user")
@RequiredArgsConstructor
public class SendUserMessageController {

    private final SendUserMessageUseCase useCase;

    @PostMapping("/send")
    public ResponseEntity<SupportMessageDto> sendMessage(Principal principal, HttpServletRequest httpRequest, @Valid @RequestBody SendMessageRequest request) {
        String email = resolveEmail(principal, httpRequest);
        return ResponseEntity.ok(useCase.sendMessageFromUser(email, request));
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
