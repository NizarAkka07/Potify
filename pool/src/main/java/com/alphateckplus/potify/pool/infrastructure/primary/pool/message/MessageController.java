package com.alphateckplus.potify.pool.infrastructure.primary.pool.message;

import com.alphateckplus.potify.pool.application_service.primary.pool.message.MessageService;
import com.alphateckplus.potify.pool.domain.model.Message;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.MessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Ajouter un message de soutien")
    public ResponseEntity<Message> addMessage(@RequestBody MessageRequest request) {
        Message message = Message.builder()
                .poolId(request.poolId())
                .userId(request.userId())
                .content(request.content())
                .isPublic(request.isPublic())
                .build();
        return ResponseEntity.ok(messageService.addMessage(message));
    }

    @GetMapping("/pool/{poolId}")
    @Operation(summary = "Lister les messages d'une cagnotte")
    public ResponseEntity<List<Message>> getPoolMessages(@PathVariable String poolId) {
        return ResponseEntity.ok(messageService.getPoolMessages(poolId));
    }

    @PostMapping("/{messageId}/react")
    @Operation(summary = "Ajouter ou supprimer une reaction")
    public ResponseEntity<Void> toggleReaction(
            @PathVariable String messageId,
            @RequestParam String userId,
            @RequestParam String type) {
        messageService.toggleReaction(messageId, userId, type);
        return ResponseEntity.ok().build();
    }
}
