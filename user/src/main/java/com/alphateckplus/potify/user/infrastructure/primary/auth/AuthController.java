package com.alphateckplus.potify.user.infrastructure.primary.auth;

import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.AuthResponse;
import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.LoginRequest;
import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.RegisterRequest;
import com.alphateckplus.potify.user.application_service.primary.command.CreateUserCommand;
import com.alphateckplus.potify.user.application_service.primary.user.create_user.CreateUserService;
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

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody RegisterRequest request) {
        CreateUserCommand command = new CreateUserCommand(
                request.getFullName(),
                request.getEmail(),
                request.getPassword()
        );
        createUserService.execute(command);
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
}
