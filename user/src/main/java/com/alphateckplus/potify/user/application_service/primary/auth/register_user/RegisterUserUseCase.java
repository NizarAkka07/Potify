package com.alphateckplus.potify.user.application_service.primary.auth.register_user;

import com.alphateckplus.potify.user.application_service.primary.auth.dto.RegisterRequest;
import com.alphateckplus.potify.user.application_service.secondary.notification.NotificationPort;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cas d'utilisation pour l'inscription d'un nouvel utilisateur.
 */
@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final NotificationPort notificationPort;

    @Transactional
    public void execute(RegisterRequest request) {
        if (userRepositoryPort.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        String token = java.util.UUID.randomUUID().toString();
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.PENDING_VERIFICATION)
                .enabled(false)
                .verificationToken(token)
                .accountNonLocked(true)
                .failedAttempts(0)
                .build();

        userRepositoryPort.save(user);
        
        // Envoi du mail de verification reel
        notificationPort.sendVerificationEmail(user.getEmail(), user.getFullName(), token);

        // TODO: Générer un SecurityToken pour la vérification d'email et envoyer un email
    }
}
