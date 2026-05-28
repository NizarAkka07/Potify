package com.alphateckplus.potify.user.infrastructure.primary.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO HTTP pour la requete de mise a jour du profil.
 * Valide les contraintes d'entree avant qu'elles n'atteignent le coeur metier.
 */
public record UpdateProfileRequest(
    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(max = 120, message = "Le nom ne doit pas depasser 120 caracteres")
    String fullName
) {}
