package com.alphateckplus.potify.user.infrastructure.primary.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour la réponse d'authentification contenant les tokens et les infos utilisateur.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String id;
    private String email;
    private List<String> roles;
    private String fullName;
    private String avatarUrl;
    private boolean adminVerification;
}

