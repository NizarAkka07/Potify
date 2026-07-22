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

import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.domain.model.UserStatus;
import com.alphateckplus.potify.user.infrastructure.primary.dto.auth.GoogleLoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Façade d'infrastructure pour gérer l'authentification Spring Security, OAuth2 Google et la génération de JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final com.alphateckplus.potify.user.application_service.primary.user.refresh_token.RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

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

        // Génération du Refresh Token réel en base
        String refreshToken = refreshTokenService.createRefreshToken(user.getId()); 

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .email(user.getEmail())
                .roles(roles)
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .adminVerification(user.isAdminVerification())
                .build();
    }

    /**
     * Authentification ou Inscription 1-clic via Google OAuth2.
     */
    @Transactional
    public AuthResponse authenticateGoogle(GoogleLoginRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> googlePayload = null;

        try {
            if (request.getIdToken().startsWith("ya29.")) {
                // Access Token de la Pop-up OAuth2 -> endpoint userinfo avec Header Authorization Bearer
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setBearerAuth(request.getIdToken());
                org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
                org.springframework.http.ResponseEntity<Map> response = restTemplate.exchange(
                        "https://www.googleapis.com/oauth2/v3/userinfo",
                        org.springframework.http.HttpMethod.GET,
                        entity,
                        Map.class
                );
                googlePayload = response.getBody();
            } else {
                // ID Token JWT de OneTap/Credential -> endpoint tokeninfo
                String tokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + request.getIdToken();
                googlePayload = restTemplate.getForObject(tokenInfoUrl, Map.class);
            }
        } catch (Exception e) {
            System.err.println("Erreur validation Google Token: " + e.getMessage());
            try {
                String tokenParam = request.getIdToken().startsWith("ya29.") ? "access_token=" : "id_token=";
                googlePayload = restTemplate.getForObject("https://oauth2.googleapis.com/tokeninfo?" + tokenParam + request.getIdToken(), Map.class);
            } catch (Exception ex) {
                System.err.println("Erreur fallback Google Token: " + ex.getMessage());
                throw new BadCredentialsException("Token Google invalide ou expiré.");
            }
        }

        if (googlePayload == null || !googlePayload.containsKey("email")) {
            throw new BadCredentialsException("Impossible de vérifier les informations de l'utilisateur Google.");
        }

        String email = (String) googlePayload.get("email");
        String name = (String) googlePayload.getOrDefault("name", email.split("@")[0]);
        String picture = (String) googlePayload.get("picture");

        Optional<User> existingUserOpt = userRepositoryPort.findByEmail(email);
        User user;

        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            boolean modified = false;

            // Si le compte était en attente de vérification par mail, Google l'a déjà vérifié
            if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
                user.setStatus(UserStatus.ACTIVE);
                user.setEnabled(true);
                modified = true;
            }

            // Met à jour la photo de profil si elle n'existe pas encore
            if ((user.getAvatarUrl() == null || user.getAvatarUrl().isBlank()) && picture != null) {
                user.setAvatarUrl(picture);
                modified = true;
            }

            if (modified) {
                userRepositoryPort.save(user);
            }
        } else {
            // Création automatique du compte utilisateur via Google
            user = User.builder()
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .status(UserStatus.ACTIVE)
                    .enabled(true)
                    .accountNonLocked(true)
                    .avatarUrl(picture)
                    .build();

            user = userRepositoryPort.save(user);
            final String newUserId = user.getId();

            roleRepositoryPort.findByName("USER").ifPresent(role ->
                    userRepositoryPort.addRoleToUser(newUserId, role.getId())
            );

            // Associe d'éventuelles contributions invité passées
            userRepositoryPort.associateContributionsToUser(email, newUserId);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtUtils.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();

        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .email(user.getEmail())
                .roles(roles)
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .adminVerification(user.isAdminVerification())
                .build();
    }
}
