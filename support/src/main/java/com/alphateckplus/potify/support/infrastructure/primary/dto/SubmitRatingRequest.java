package com.alphateckplus.potify.support.infrastructure.primary.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitRatingRequest {

    @Min(value = 1, message = "La note minimale est de 1 étoile")
    @Max(value = 5, message = "La note maximale est de 5 étoiles")
    private int score;

    private String comment;
}
