package com.alphateckplus.potify.user.infrastructure.primary.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête de connexion/inscription via Google OAuth2.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleLoginRequest {

    @NotBlank(message = "Le token Google est obligatoire.")
    private String idToken;
}
