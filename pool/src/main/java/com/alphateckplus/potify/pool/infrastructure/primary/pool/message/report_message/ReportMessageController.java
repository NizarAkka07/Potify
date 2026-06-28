package com.alphateckplus.potify.pool.infrastructure.primary.pool.message.report_message;

import com.alphateckplus.potify.pool.application_service.primary.pool.message.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
public class ReportMessageController {

    private final MessageService messageService;

    @PostMapping("/{messageId}/report")
    @Operation(summary = "Signaler un message")
    public ResponseEntity<Void> reportMessage(
            @PathVariable String messageId,
            @RequestParam String userId,
            @RequestParam(required = false, defaultValue = "Contenu inapproprié") String reason) {
        messageService.reportMessage(messageId, userId, reason);
        return ResponseEntity.ok().build();
    }
}
