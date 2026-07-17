package com.alphateckplus.potify.pool.infrastructure.primary.pool.message.get_reported_messages;

import com.alphateckplus.potify.pool.application_service.primary.pool.message.MessageService;
import com.alphateckplus.potify.pool.domain.model.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
public class GetReportedMessagesController {

    private final MessageService messageService;

    @GetMapping("/reported")
    @PreAuthorize("hasAuthority('POOL_MODERATE') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MODERATEUR') or hasAuthority('ROLE_ADMIN_POOL')")
    @Operation(summary = "Obtenir la liste des messages signalés (Modération)")
    public ResponseEntity<List<Message>> getReportedMessages() {
        return ResponseEntity.ok(messageService.getReportedMessages());
    }
}
