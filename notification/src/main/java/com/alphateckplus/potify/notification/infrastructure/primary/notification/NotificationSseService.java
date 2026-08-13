package com.alphateckplus.potify.notification.infrastructure.primary.notification;

import com.alphateckplus.potify.notification.domain.model.Notification;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Service de diffusion des notifications utilisateur via Server-Sent Events (SSE).
 */
@Service
@Slf4j
public class NotificationSseService {

    private final Map<String, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId) {
        log.info("Client SSE connecté pour l'utilisateur : {}", userId);
        SseEmitter emitter = new SseEmitter(0L); // 0L = Pas de timeout d'expiration automatique

        userEmitters.computeIfAbsent(userId, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));

        // Envoi d'un message initial de connexion
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connecté au flux de notification SSE pour l'utilisateur " + userId));
        } catch (IOException e) {
            removeEmitter(userId, emitter);
        }

        return emitter;
    }

    public void sendNotification(String userId, Notification notification) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null && !emitters.isEmpty()) {
            log.info("Diffusion SSE de notification à l'utilisateur {} ({} émetteurs)", userId, emitters.size());
            List<SseEmitter> deadEmitters = new ArrayList<>();
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(notification));
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            }
            emitters.removeAll(deadEmitters);
        }
    }

    private void removeEmitter(String userId, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            log.info("Émetteur SSE retiré pour l'utilisateur : {}", userId);
        }
    }
}
