package com.alphateckplus.potify.pool.infrastructure.secondary.notification;

import com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationAdapter implements NotificationPort {

    private final JavaMailSender mailSender;

    @Override
    public void sendInvitationEmail(String email, String poolName, String token) {
        String invitationUrl = "http://localhost:9000/#/pool/invitation?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Invitation à rejoindre la cagnotte : " + poolName);
        message.setText("Bonjour,\n\n" +
                "Vous avez été invité à rejoindre la cagnotte \"" + poolName + "\" sur Potify.\n\n" +
                "Pour accepter l'invitation, veuillez cliquer sur le lien suivant :\n" +
                invitationUrl + "\n\n" +
                "L'équipe Potify");
        
        try {
            System.out.println(">>> [MAIL] Envoi de l'invitation à : " + email);
            mailSender.send(message);
            System.out.println(">>> [MAIL] Invitation envoyée avec succès !");
        } catch (Exception e) {
            System.err.println(">>> [MAIL] ERREUR lors de l'envoi de l'invitation : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
