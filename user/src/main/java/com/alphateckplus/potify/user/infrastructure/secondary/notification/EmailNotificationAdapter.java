package com.alphateckplus.potify.user.infrastructure.secondary.notification;

import com.alphateckplus.potify.user.application_service.secondary.notification.NotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationAdapter implements NotificationPort {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String email, String fullName, String token) {
        String verificationUrl = "http://localhost:9000/#/verify-email?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Bienvenue sur Potify - Vérifiez votre compte");
        message.setText("Bonjour " + fullName + ",\n\n" +
                "Merci de vous être inscrit sur Potify. Pour activer votre compte, veuillez cliquer sur le lien suivant :\n" +
                verificationUrl + "\n\n" +
                "Si vous n'êtes pas à l'origine de cette inscription, ignorez cet email.\n\n" +
                "L'équipe Potify");
        
        System.out.println(">>> [MAIL] [DEV MODE] Le lien de vérification pour " + email + " est : " + verificationUrl);
        try {
            System.out.println(">>> [MAIL] Envoi de l'email à : " + email);
            mailSender.send(message);
            System.out.println(">>> [MAIL] Email envoyé avec succès !");
        } catch (Exception e) {
            System.err.println(">>> [MAIL] ERREUR lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
            // Ne pas lever d'exception pour éviter de bloquer la création de compte si l'email ne s'envoie pas
        }
    }

    @Override
    public void sendPasswordResetEmail(String email, String fullName, String token) {
        String resetUrl = "http://localhost:9000/#/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Potify - Réinitialisation de votre mot de passe");
        message.setText("Bonjour " + fullName + ",\n\n" +
                "Vous avez demandé la réinitialisation de votre mot de passe. " +
                "Cliquez sur le lien suivant pour définir un nouveau mot de passe :\n" +
                resetUrl + "\n\n" +
                "Ce lien expire dans 1 heure.\n\n" +
                "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email. " +
                "Votre mot de passe actuel reste inchangé.\n\n" +
                "L'équipe Potify");

        System.out.println(">>> [MAIL] [DEV MODE] Le lien de reset password pour " + email + " est : " + resetUrl);
        try {
            System.out.println(">>> [MAIL] Envoi de l'email de reset password à : " + email);
            mailSender.send(message);
            System.out.println(">>> [MAIL] Email de reset password envoyé avec succès !");
        } catch (Exception e) {
            System.err.println(">>> [MAIL] ERREUR lors de l'envoi de l'email de reset password : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
