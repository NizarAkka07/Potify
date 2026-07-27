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
public class MarkAsReadRequest {

    @NotBlank(message = "L'ID de la conversation est obligatoire")
    private String conversationId;
}
