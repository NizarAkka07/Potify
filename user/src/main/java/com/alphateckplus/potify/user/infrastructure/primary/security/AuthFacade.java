package com.alphateckplus.potify.user.infrastructure.primary.security;

import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.AuthResponse;
import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.LoginRequest;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Façade d'infrastructure pour gérer l'authentification Spring Security et la génération de JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepositoryPort.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));

        if (user.getStatus() == com.alphateckplus.potify.user.domain.model.UserStatus.PENDING_VERIFICATION) {
            throw new BadCredentialsException("Veuillez vérifier votre email avant de vous connecter.");
        }

        // Vérifier si le compte est verrouillé
        if (!user.isAccountNonLocked()) {
            if (user.isLockExpired()) {
                user.resetFailedAttempts();
                userRepositoryPort.save(user);
            } else {
                throw new LockedException("Compte verrouillé. Veuillez réessayer plus tard.");
            }
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            user.incrementFailedAttempts();
            userRepositoryPort.save(user);
            throw e;
        }

        // Succès de l'authentification
        user.resetFailedAttempts();
        userRepositoryPort.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtUtils.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();

        // TODO: Implémenter la génération du Refresh Token en base
        String refreshToken = "mock-refresh-token"; 

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}
