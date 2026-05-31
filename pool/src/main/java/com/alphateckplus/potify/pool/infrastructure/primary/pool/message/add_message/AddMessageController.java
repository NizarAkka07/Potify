package com.alphateckplus.potify.pool.infrastructure.primary.pool.message.add_message;

import com.alphateckplus.potify.pool.application_service.primary.pool.message.MessageService;
import com.alphateckplus.potify.pool.domain.model.Message;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.MessageRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.message.PoolSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
public class AddMessageController {

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
}
