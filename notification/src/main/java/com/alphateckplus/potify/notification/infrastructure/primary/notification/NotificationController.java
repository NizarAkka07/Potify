package com.alphateckplus.potify.notification.infrastructure.primary.notification;

import com.alphateckplus.potify.notification.application_service.primary.notification.GetNotificationsService;
import com.alphateckplus.potify.notification.application_service.primary.notification.MarkNotificationReadService;
import com.alphateckplus.potify.notification.domain.model.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.notification.application_service.primary.notification.CreateNotificationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final GetNotificationsService getNotificationsService;
    private final MarkNotificationReadService markNotificationReadService;
    private final CreateNotificationService createNotificationService;
    private final UserEntityRepository userEntityRepository;
    private final JavaMailSender mailSender;
    private final NotificationSseService notificationSseService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminWarningRequest {
        private String userId;
        private String title;
        private String message;
    }

    @GetMapping(value = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@PathVariable String userId) {
        return notificationSseService.subscribe(userId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(getNotificationsService.execute(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(markNotificationReadService.execute(id));
    }

    @PostMapping("/warn")
    public ResponseEntity<Notification> sendAdminWarning(@RequestBody AdminWarningRequest request) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String title = (request.getTitle() != null && !request.getTitle().isBlank())
                ? request.getTitle()
                : "Avertissement de la modération";

        String message = (request.getMessage() != null && !request.getMessage().isBlank())
                ? request.getMessage()
                : "Un administrateur vous a adressé un avertissement suite à un non-respect des règles de la plateforme.";

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .type("ADMIN_WARNING")
                .channel("NOTIF_APP")
                .title(title)
                .content(message)
                .status("ACTIVE")
                .build();

        Notification created = createNotificationService.execute(notification);

        // Diffusion SSE en temps réel
        try {
            notificationSseService.sendNotification(request.getUserId(), created);
        } catch (Exception e) {
            log.error("Erreur diffusion SSE avertissement admin", e);
        }

        // Tentative d'envoi d'email
        try {
            userEntityRepository.findById(request.getUserId()).ifPresent(user -> {
                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setFrom("nizarakka07@gmail.com");
                    mailMessage.setTo(user.getEmail());
                    mailMessage.setSubject(title);
                    mailMessage.setText(message);
                    mailSender.send(mailMessage);
                    log.info("Email d'avertissement envoyé avec succès à : {}", user.getEmail());
                }
            });
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email d'avertissement pour l'utilisateur {}", request.getUserId(), e);
        }

        return ResponseEntity.ok(created);
    }
}
