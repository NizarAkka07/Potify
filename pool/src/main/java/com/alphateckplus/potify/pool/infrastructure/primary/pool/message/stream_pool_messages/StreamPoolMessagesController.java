package com.alphateckplus.potify.pool.infrastructure.primary.pool.message.stream_pool_messages;

import com.alphateckplus.potify.pool.infrastructure.primary.pool.message.PoolSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
public class StreamPoolMessagesController {

    private final PoolSseService sseService;

    @GetMapping(value = "/pool/{poolId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Flux de messages en temps reel (SSE)")
    public SseEmitter streamPoolMessages(@PathVariable String poolId) {
        return sseService.subscribe(poolId);
    }
}
