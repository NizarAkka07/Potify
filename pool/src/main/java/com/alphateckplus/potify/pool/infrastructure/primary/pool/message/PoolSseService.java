package com.alphateckplus.potify.pool.infrastructure.primary.pool.message;

import com.alphateckplus.potify.pool.domain.model.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PoolSseService {

    private final Map<String, List<SseEmitter>> poolEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String poolId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Pas de timeout pour la demo
        
        poolEmitters.computeIfAbsent(poolId, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(poolId, emitter));
        emitter.onTimeout(() -> removeEmitter(poolId, emitter));
        emitter.onError((e) -> removeEmitter(poolId, emitter));

        return emitter;
    }

    public void broadcastMessage(String poolId, Message message) {
        List<SseEmitter> emitters = poolEmitters.get(poolId);
        if (emitters != null) {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data(message));
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            }
            emitters.removeAll(deadEmitters);
        }
    }

    private void removeEmitter(String poolId, SseEmitter emitter) {
        List<SseEmitter> emitters = poolEmitters.get(poolId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }
}
