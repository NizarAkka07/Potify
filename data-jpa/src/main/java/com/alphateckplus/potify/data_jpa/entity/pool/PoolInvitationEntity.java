package com.alphateckplus.potify.data_jpa.entity.pool;

import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Entite pour gerer les invitations aux cagnottes privees (Tontines).
 */
@Entity
@Table(name = "pool_invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = "pool")
public class PoolInvitationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id", nullable = false)
    private PoolEntity pool;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING"; // PENDING, ACCEPTED, REJECTED

    @Column(name = "token", unique = true, length = 100)
    private String token; // Token unique pour le lien d'invitation
}
