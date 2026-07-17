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

    @DeleteMapping("/{messageId}")
    @PreAuthorize("hasAuthority('POOL_MODERATE') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MODERATEUR') or hasAuthority('ROLE_ADMIN_POOL')")
    @Operation(summary = "Supprimer un commentaire (Modération)")
    public ResponseEntity<Void> deleteMessage(@PathVariable String messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
