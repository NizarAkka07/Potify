package com.alphateckplus.potify.pool.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Agregat metier Contribution (Don / Participation).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contribution {

    /** Identifiant unique de la contribution. */
    private String id;

    /** Identifiant de la cagnotte associee. */
    private String poolId;

    /** Identifiant de l'utilisateur (null si visiteur). */
    private String userId;

    /** Email du contributeur (obligatoire si visiteur). */
    private String contributorEmail;

    /** Nom d'affichage du contributeur. */
    private String contributorName;

    /** Montant de la contribution. */
    private BigDecimal amount;

    /** Message optionnel joint a la contribution. */
    private String message;

    /** Indique si la contribution doit être anonyme. */
    private boolean anonymous;

    /** Statut actuel de la transaction. */
    private ContributionStatus status;

    /** Moyen de paiement utilise. */
    private String paymentMethod;

    /** Horodatage de creation. */
    private Instant createdAt;

    /** Horodatage de validation/succes. */
    private Instant validatedAt;

    // --- Logique Metier ---

    /**
     * Verifie si la contribution est validee.
     */
    public boolean isSuccessful() {
        return ContributionStatus.REUSSIE.equals(this.status);
    }
}
