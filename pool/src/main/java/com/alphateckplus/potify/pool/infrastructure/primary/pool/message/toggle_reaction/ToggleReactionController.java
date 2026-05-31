package com.alphateckplus.potify.pool.infrastructure.primary.pool.message.toggle_reaction;

import com.alphateckplus.potify.pool.application_service.primary.pool.message.MessageService;
import com.alphateckplus.potify.pool.domain.model.Message;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.message.PoolSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
public class ToggleReactionController {

    private final MessageService messageService;
    private final PoolSseService sseService;

    @PostMapping("/{messageId}/react")
    @Operation(summary = "Ajouter ou supprimer une reaction")
    public ResponseEntity<Void> toggleReaction(
            @PathVariable String messageId,
            @RequestParam String userId,
            @RequestParam String type) {
        messageService.toggleReaction(messageId, userId, type);
        // Recuperer le message mis a jour avec les nouvelles reactions
        Message updatedMessage = messageService.getMessage(messageId);
        sseService.broadcastMessage(updatedMessage.getPoolId(), updatedMessage);
        return ResponseEntity.ok().build();
    }
}
