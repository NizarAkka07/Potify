package com.alphateckplus.potify.pool.infrastructure.primary.pool.message.delete_message;

import com.alphateckplus.potify.pool.application_service.primary.pool.message.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
public class DeleteMessageController {

    private final MessageService messageService;

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DeleteMessageRequest {
        private String reason;
    }

    @DeleteMapping("/{messageId}")
    @PreAuthorize("hasAuthority('MESSAGE_DELETE') or hasRole('SUPER_ADMIN') or hasRole('MODERATEUR')")
    @Operation(summary = "Supprimer un commentaire avec motif/avertissement (Modération)")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable String messageId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String reason,
            @org.springframework.web.bind.annotation.RequestBody(required = false) DeleteMessageRequest request) {
        String finalReason = (reason != null && !reason.isBlank()) ? reason : (request != null ? request.getReason() : null);
        messageService.deleteMessage(messageId, finalReason);
        return ResponseEntity.noContent().build();
    }
}
