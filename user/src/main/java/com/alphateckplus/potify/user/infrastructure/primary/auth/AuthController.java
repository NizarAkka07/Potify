package com.alphateckplus.potify.user.infrastructure.primary.auth;

import com.alphateckplus.potify.user.application_service.primary.auth.dto.AuthResponse;
import com.alphateckplus.potify.user.application_service.primary.auth.dto.LoginRequest;
import com.alphateckplus.potify.user.application_service.primary.auth.dto.RegisterRequest;
import com.alphateckplus.potify.user.application_service.primary.auth.login_user.LoginUserUseCase;
import com.alphateckplus.potify.user.application_service.primary.auth.register_user.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody RegisterRequest request) {
        registerUserUseCase.execute(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUserUseCase.execute(request));
    }
}
