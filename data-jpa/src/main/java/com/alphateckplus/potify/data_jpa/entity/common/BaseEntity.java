package com.alphateckplus.potify.data_jpa.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Classe parente pour toutes les entites JPA du module.
 *
 * <p>Objectif architectural (SOLID):
 * - SRP (Single Responsibility): centraliser les champs techniques communs
 *   (id, createdAt, updatedAt) dans un seul endroit.
 * - OCP (Open/Closed): les nouvelles entites reutilisent ce socle sans dupliquer
 *   le code.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

    /**
     * Cle primaire technique.
     *
     * <p>GenerationType.UUID (Hibernate 6+) permet de deleguer la creation de l'id
     * au provider JPA sans code manuel.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    @EqualsAndHashCode.Include
    private String id;

    /**
     * Date de creation automatiquement renseignee a l'insertion.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Date de derniere modification automatiquement mise a jour a chaque update.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
