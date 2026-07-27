package com.alphateckplus.potify.support.infrastructure.primary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferConversationRequest {

    @NotBlank(message = "L'ID du nouvel agent administrateur est obligatoire")
    private String targetAdminId;

    private String reason;
}
