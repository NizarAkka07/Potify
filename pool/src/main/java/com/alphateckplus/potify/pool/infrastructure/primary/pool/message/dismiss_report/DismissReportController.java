package com.alphateckplus.potify.pool.infrastructure.primary.pool.message.dismiss_report;

import com.alphateckplus.potify.pool.application_service.primary.pool.message.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
public class DismissReportController {

    private final MessageService messageService;

    @PostMapping("/{messageId}/dismiss")
    @Operation(summary = "Rejeter le signalement d'un message")
    public ResponseEntity<Void> dismissReport(@PathVariable String messageId) {
        messageService.dismissReport(messageId);
        return ResponseEntity.ok().build();
    }
}
