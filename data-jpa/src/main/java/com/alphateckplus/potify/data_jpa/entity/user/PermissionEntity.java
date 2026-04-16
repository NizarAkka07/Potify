package com.alphateckplus.potify.data_jpa.entity.user;

import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Entite de permission applicative.
 *
 * <p>Exemples: USER_READ, USER_WRITE, PAYMENT_REFUND.
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = "roles")
public class PermissionEntity extends BaseEntity {

    /**
     * Code unique et stable de permission.
     *
     * <p>unique=true protege la base contre les doublons.
     */
    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    /**
     * Cote inverse de la relation many-to-many avec les roles.
     *
     * <p>mappedBy evite de dupliquer la table de jointure dans les deux classes.
     * fetch=LAZY limite le chargement inutile pour garder un couplage faible entre
     * cas d'usage.
     */
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<RoleEntity> roles = new HashSet<>();
}
