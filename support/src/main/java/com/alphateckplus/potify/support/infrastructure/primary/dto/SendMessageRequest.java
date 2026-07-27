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
public class SendMessageRequest {

    @NotBlank(message = "L'ID de la conversation est obligatoire")
    private String conversationId;

    @NotBlank(message = "Le contenu du message ne peut être vide")
    private String content;

    private String attachmentUrl;
    private String attachmentType;
}
