package com.alphateckplus.potify.notification.infrastructure.primary.notification;

import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.notification.application_service.primary.notification.CreateNotificationService;
import com.alphateckplus.potify.notification.domain.model.Notification;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Consommateur d'événements Kafka pour les notifications Potify.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaConsumer {

    private final CreateNotificationService createNotificationService;
    private final UserEntityRepository userEntityRepository;
    private final JavaMailSender mailSender;
    private final NotificationSseService notificationSseService;

    @KafkaListener(topics = "potify-notifications", groupId = "potify-group")
    public void listen(Map<String, Object> payload) {
        log.info("Received notification event: {}", payload);
        try {
            String userId = (String) payload.get("userId");
            String type = (String) payload.get("type");
            String title = (String) payload.get("title");
            String content = (String) payload.get("content");
            String channel = (String) payload.get("channel");

            String directEmail = (String) payload.get("email");

            if (userId != null) {
                Notification notification = Notification.builder()
                        .userId(userId)
                        .type(type)
                        .title(title)
                        .content(content)
                        .channel(channel != null ? channel : "NOTIF_APP")
                        .status("ACTIVE")
                        .build();

                Notification created = createNotificationService.execute(notification);
                log.info("Successfully processed and saved notification for user: {}", userId);

                // Diffusion en temps réel via SSE
                try {
                    notificationSseService.sendNotification(userId, created);
                } catch (Exception e) {
                    log.error("Erreur lors de la diffusion SSE pour l'utilisateur {}", userId, e);
                }

                // Envoi de l'email à l'utilisateur
                userEntityRepository.findById(userId).ifPresentOrElse(user -> {
                    String email = user.getEmail();
                    if (email != null && !email.isBlank()) {
                        sendEmail(email, title, content);
                    } else {
                        log.warn("L'utilisateur {} n'a pas d'email configuré.", userId);
                    }
                }, () -> log.warn("Utilisateur {} introuvable pour envoi de notification.", userId));
            } else if (directEmail != null && !directEmail.isBlank()) {
                log.info("Sending notification directly to guest email: {}", directEmail);
                sendEmail(directEmail, title, content);
            } else {
                log.warn("Notification event has neither userId nor direct email, skipping.");
            }
        } catch (Exception e) {
            log.error("Failed to process notification event: {}", payload, e);
        }
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("nizarakka07@gmail.com");
            message.setTo(to);
            message.setSubject(subject != null ? subject : "Notification Potify");
            message.setText(text);
            mailSender.send(message);
            log.info("Email notification successfully sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email notification to: {}", to, e);
        }
    }
}
