package com.alphateckplus.potify.user.application_service.primary.user.forgot_password;

import com.alphateckplus.potify.user.application_service.secondary.notification.NotificationPort;
import com.alphateckplus.potify.user.application_service.secondary.user.SecurityTokenRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation du cas d'utilisation "Mot de passe oublie".
 *
 * <p>Genere un token de type PASSWORD_RESET, le persiste et envoie un email
 * avec le lien de reinitialisation. Retourne toujours un succes pour ne pas
 * reveler si l'email existe dans le systeme (securite).
 */
public class DefaultForgotPasswordService implements ForgotPasswordService {

    private final UserRepositoryPort userRepositoryPort;
    private final SecurityTokenRepositoryPort securityTokenRepositoryPort;
    private final NotificationPort notificationPort;

    public DefaultForgotPasswordService(
            UserRepositoryPort userRepositoryPort,
            SecurityTokenRepositoryPort securityTokenRepositoryPort,
            NotificationPort notificationPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.securityTokenRepositoryPort = securityTokenRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void execute(String email) {
        Optional<User> userOpt = userRepositoryPort.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Ne pas reveler que l'email n'existe pas (securite)
            return;
        }

        User user = userOpt.get();

        // Generer un token unique
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        // Persister le token
        securityTokenRepositoryPort.save(token, "PASSWORD_RESET", user.getId(), expiryDate);

        // Envoyer l'email
        notificationPort.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);
    }
}
