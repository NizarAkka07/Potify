package com.alphateckplus.potify.data_jpa.entity.user;

// Importation de l'entité de base commune
import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
// Importations pour la persistance JPA
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
// Importation pour la gestion du temps (Utilisation de Instant pour la cohérence avec BaseEntity)
import java.time.Instant;
// Importations Lombok pour réduire le code boilerplate
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Entité de persistance pour les Refresh Tokens.
 * Cette classe permet de stocker les jetons de rafraîchissement en base de données
 * pour maintenir la session utilisateur de manière sécurisée après l'expiration du JWT.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = "user")
public class RefreshTokenEntity extends BaseEntity {

    /**
     * Relation un-à-un avec l'utilisateur propriétaire du token.
     * Chargement paresseux (LAZY) pour optimiser les performances.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Le jeton de rafraîchissement lui-même.
     * Doit être unique et non nul.
     */
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    /**
     * Date et heure d'expiration du jeton.
     */
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    /**
     * Indicateur pour savoir si le jeton a été révoqué manuellement.
     */
    @Column(name = "revoked", nullable = false)
    private boolean revoked;
}
