package com.alphateckplus.potify.user.application_service.primary.auth.register_user;

import com.alphateckplus.potify.user.application_service.primary.auth.dto.RegisterRequest;
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

    @Transactional
    public void execute(RegisterRequest request) {
        if (userRepositoryPort.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE) // Par défaut, on peut mettre PENDING si on force la vérification email
                .enabled(true) // Activé par défaut pour le test (à changer quand l'email sera prêt)
                .accountNonLocked(true)
                .failedAttempts(0)
                .build();

        userRepositoryPort.save(user);

        // TODO: Générer un SecurityToken pour la vérification d'email et envoyer un email
    }
}
