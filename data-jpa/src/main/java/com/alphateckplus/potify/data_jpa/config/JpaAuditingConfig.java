package com.alphateckplus.potify.data_jpa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Active l'audit Spring Data JPA.
 *
 * <p>Pourquoi cette classe existe:
 * - @EnableJpaAuditing active automatiquement le remplissage des champs annotes
 *   avec @CreatedDate et @LastModifiedDate.
 * - Cela evite d'ecrire du code manuel dans chaque service pour gerer les dates.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Classe de configuration volontairement vide:
    // la seule presence de l'annotation suffit pour activer le mecanisme.
}
