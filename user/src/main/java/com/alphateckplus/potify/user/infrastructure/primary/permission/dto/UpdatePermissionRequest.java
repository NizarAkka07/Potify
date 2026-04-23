package com.alphateckplus.potify.user.infrastructure.primary.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO HTTP pour la mise a jour d'une permission.
 */
public record UpdatePermissionRequest(
    @NotBlank(message = "Le code permission est obligatoire")
    @Size(max = 100, message = "Le code ne doit pas depasser 100 caracteres")
    String code,

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 255, message = "La description ne doit pas depasser 255 caracteres")
    String description
) {
}
