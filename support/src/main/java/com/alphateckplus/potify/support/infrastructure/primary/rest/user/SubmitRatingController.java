package com.alphateckplus.potify.support.infrastructure.primary.rest.user;

import com.alphateckplus.potify.support.application_service.primary.user.submit_rating.SubmitRatingUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SubmitRatingRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/user")
@RequiredArgsConstructor
public class SubmitRatingController {

    private final SubmitRatingUseCase useCase;

    @PostMapping("/rate/{conversationId}")
    public ResponseEntity<Void> submitRating(Principal principal, HttpServletRequest httpRequest, @PathVariable String conversationId, @Valid @RequestBody SubmitRatingRequest request) {
        String email = resolveEmail(principal, httpRequest);
        useCase.submitRating(email, conversationId, request);
        return ResponseEntity.ok().build();
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
