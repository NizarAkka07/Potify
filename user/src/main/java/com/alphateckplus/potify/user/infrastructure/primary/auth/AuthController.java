package com.alphateckplus.potify.user.infrastructure.primary.auth;

import com.alphateckplus.potify.user.application_service.primary.auth.dto.AuthResponse;
import com.alphateckplus.potify.user.application_service.primary.auth.dto.LoginRequest;
import com.alphateckplus.potify.user.application_service.primary.auth.dto.RegisterRequest;
import com.alphateckplus.potify.user.application_service.primary.auth.login_user.LoginUserUseCase;
import com.alphateckplus.potify.user.application_service.primary.auth.register_user.RegisterUserUseCase;
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

    private final LoginUserUseCase loginUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final VerifyEmailService verifyEmailService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody RegisterRequest request) {
        registerUserUseCase.execute(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUserUseCase.execute(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean verified = verifyEmailService.execute(token);
        if (verified) {
            return ResponseEntity.ok("Email verifié avec succès ! Vous pouvez maintenant vous connecter.");
        }
        return ResponseEntity.badRequest().body("Lien de verification invalide ou expiré.");
    }
}
