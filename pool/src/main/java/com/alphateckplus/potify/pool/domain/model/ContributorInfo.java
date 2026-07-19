package com.alphateckplus.potify.pool.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.EqualsAndHashCode;

/**
 * Modèle simple pour stocker les informations de contact d'un contributeur.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContributorInfo {
    private String userId;
    private String email;
}
