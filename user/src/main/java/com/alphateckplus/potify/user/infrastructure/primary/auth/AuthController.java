package com.alphateckplus.potify.user.infrastructure.primary.auth;

import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.AuthResponse;
import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.ForgotPasswordRequest;
import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.LoginRequest;
import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.RegisterRequest;
import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.ResetPasswordRequest;
import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.RefreshTokenRequest;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.application_service.primary.user.create_user.CreateUserService;
import com.alphateckplus.potify.user.application_service.primary.user.forgot_password.ForgotPasswordService;
import com.alphateckplus.potify.user.application_service.primary.user.reset_password.ResetPasswordService;
import com.alphateckplus.potify.user.infrastructure.primary.security.AuthFacade;
import com.alphateckplus.potify.user.application_service.primary.user.verify_email.VerifyEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST pour les opérations d'authentification.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;
    private final CreateUserService createUserService;
    private final VerifyEmailService verifyEmailService;
    private final ForgotPasswordService forgotPasswordService;
    private final ResetPasswordService resetPasswordService;
    private final com.alphateckplus.potify.user.application_service.primary.user.refresh_token.RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody RegisterRequest request) {
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .avatarUrl(request.getAvatarUrl())
                .build();
        createUserService.execute(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authFacade.authenticate(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean verified = verifyEmailService.execute(token);
        if (verified) {
            return ResponseEntity.ok("Email verifié avec succès ! Vous pouvez maintenant vous connecter.");
        }
        return ResponseEntity.badRequest().body("Lien de verification invalide ou expiré.");
    }

    /**
     * Demande de reinitialisation de mot de passe.
     * Retourne toujours 200 pour ne pas reveler si l'email existe.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordService.execute(request.getEmail());
        return ResponseEntity.ok("Si un compte existe avec cet email, un lien de réinitialisation a été envoyé.");
    }

    /**
     * Reinitialisation effective du mot de passe avec le token recu par email.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        boolean reset = resetPasswordService.execute(request.getToken(), request.getNewPassword());
        if (reset) {
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès ! Vous pouvez maintenant vous connecter.");
        }
        return ResponseEntity.badRequest().body("Lien de réinitialisation invalide ou expiré.");
    }

    /**
     * Renouvellement du token JWT.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.refreshSession(request.getRefreshToken()));
    }

    /**
     * Deconnexion (revocation du refresh token).
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }
}

