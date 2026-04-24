package com.alphateckplus.potify.user.infrastructure.primary.dto;

import com.alphateckplus.potify.user.domain.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO HTTP pour la mise a jour du profil utilisateur avec statut.
 */
public record UpdateUserRequest(
    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(max = 120, message = "Le nom complet ne doit pas depasser 120 caracteres")
    String fullName,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres")
    String email,

    @Size(min = 6, max = 255, message = "Le mot de passe doit contenir entre 6 et 255 caracteres")
    String password,

    @NotNull(message = "Le statut est obligatoire")
    UserStatus status
) {
}
