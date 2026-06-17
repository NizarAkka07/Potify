package com.alphateckplus.potify.data_jpa.entity.payment;

import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteWalletEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Contribution financiere d'un utilisateur vers une cagnotte.
 * Cette entite est partagee entre les microservices pool et payment.
 */
@Entity
@Table(name = "contributions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"pool", "user", "wallet"})
public class ContributionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cagnotte_id", nullable = false)
    private PoolEntity pool;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Rendu optionnel pour les dons de visiteurs
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private CagnotteWalletEntity wallet;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "contributor_email", length = 255)
    private String contributorEmail;

    @Column(name = "contributor_name", length = 100)
    private String contributorName;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "anonymous", nullable = false)
    private boolean anonymous;

    @Column(name = "payment_method", length = 255)
    private String paymentMethod;

    @Column(name = "fees", nullable = false, precision = 19, scale = 2)
    @lombok.Builder.Default
    private BigDecimal fees = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ContributionStatus status;
}
