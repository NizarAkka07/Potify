package com.alphateckplus.potify.support.infrastructure.primary.rest.user;

import com.alphateckplus.potify.support.application_service.primary.user.get_user_messages.GetUserMessagesUseCase;
import com.alphateckplus.potify.support.infrastructure.primary.dto.SupportMessageDto;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/user")
@RequiredArgsConstructor
public class GetUserMessagesController {

    private final GetUserMessagesUseCase useCase;

    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<List<SupportMessageDto>> getMessages(
            Principal principal,
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(useCase.getConversationMessages(conversationId, page, size));
    }
}
