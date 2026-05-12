package com.alphateckplus.potify.pool.infrastructure.primary.pool.message;

import com.alphateckplus.potify.pool.application_service.primary.pool.message.MessageService;
import com.alphateckplus.potify.pool.domain.model.Message;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.MessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
public class MessageController {

    private final MessageService messageService;
    private final PoolSseService sseService;

    @PostMapping
    @Operation(summary = "Ajouter un message de soutien")
    public ResponseEntity<Message> addMessage(@RequestBody MessageRequest request) {
        Message message = Message.builder()
                .poolId(request.poolId())
                .userId(request.userId())
                .content(request.content())
                .isPublic(request.isPublic())
                .build();
        Message saved = messageService.addMessage(message);
        sseService.broadcastMessage(request.poolId(), saved);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/pool/{poolId}")
    @Operation(summary = "Lister les messages d'une cagnotte")
    public ResponseEntity<List<Message>> getPoolMessages(@PathVariable String poolId) {
        return ResponseEntity.ok(messageService.getPoolMessages(poolId));
    }

    @GetMapping(value = "/pool/{poolId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Flux de messages en temps reel (SSE)")
    public SseEmitter streamPoolMessages(@PathVariable String poolId) {
        return sseService.subscribe(poolId);
    }

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
