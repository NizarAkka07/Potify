package com.alphateckplus.potify.pool.infrastructure.primary.pool.message.get_pool_messages;

import com.alphateckplus.potify.pool.application_service.primary.pool.message.MessageService;
import com.alphateckplus.potify.pool.domain.model.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
public class GetPoolMessagesController {

    private final MessageService messageService;

    @GetMapping("/pool/{poolId}")
    @Operation(summary = "Lister les messages d'une cagnotte")
    public ResponseEntity<List<Message>> getPoolMessages(@PathVariable String poolId) {
        return ResponseEntity.ok(messageService.getPoolMessages(poolId));
    }
}
