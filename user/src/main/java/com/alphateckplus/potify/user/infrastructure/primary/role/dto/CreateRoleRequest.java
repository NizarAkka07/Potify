package com.alphateckplus.potify.user.infrastructure.primary.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO HTTP pour la creation d'un role.
 */
public record CreateRoleRequest(
    @NotBlank(message = "Le nom du role est obligatoire")
    @Size(max = 100, message = "Le nom du role ne doit pas depasser 100 caracteres")
    String name,

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 255, message = "La description ne doit pas depasser 255 caracteres")
    String description
) {
}
