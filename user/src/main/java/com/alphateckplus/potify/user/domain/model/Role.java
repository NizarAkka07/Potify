package com.alphateckplus.potify.user.domain.model;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Modele metier d'un role utilisateur.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private String id;
    private String name;
    private String description;

    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}
