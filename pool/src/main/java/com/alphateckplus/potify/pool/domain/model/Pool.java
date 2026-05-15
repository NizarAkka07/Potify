package com.alphateckplus.potify.pool.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Agregat metier Pool (Cagnotte) de l'architecture hexagonale.
 *
 * <p>Cette classe gere la logique pure des cagnottes, incluant les regles
 * de transition d'etat et les specificites des tontines.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pool {

    /** Identifiant unique de la cagnotte. */
    private String id;

    /** Identifiant de l'utilisateur createur/proprietaire. */
    private String ownerId;

    /** Nom complet du proprietaire. */
    private String ownerName;

    /** Identifiant de la cagnotte parente (si applicable). */
    private String parentId;

    /** Titre de la collecte. */
    private String title;

    /** Description detaillee de l'objectif. */
    private String description;

    /** Categorie de la collecte. */
    private String category;

    /** Montant cible a atteindre. */
    private BigDecimal goalAmount;

    /** Montant actuellement collecte. */
    private BigDecimal currentAmount;

    /** Statut actuel du cycle de vie. */
    private PoolStatus status;

    /** Type de la cagnotte (Public ou Tontine privee). */
    private PoolType type;

    /** Liste des identifiants utilisateurs invites (pour le mode Tontine). */
    @Builder.Default
    private Set<String> invitedUserIds = new HashSet<>();

    /** Horodatage de creation. */
    private Instant createdAt;

    /** Horodatage de derniere modification. */
    private Instant updatedAt;

    /** Contenu binaire de l'image (stockage DB). */
    private byte[] imageContent;

    /** Type MIME de l'image (ex: image/png). */
    private String imageContentType;

    /** URL d'une vidéo de présentation (YouTube, Vimeo, etc.). */
    private String videoUrl;

    /** Portefeuille financier de la cagnotte. */
    private Wallet wallet;

    /** Liste des cagnottes filles (phases). */
    @Builder.Default
    private java.util.List<Pool> children = new java.util.ArrayList<>();

    /** Liste des invitations pour cette cagnotte. */
    private java.util.List<Invitation> invitations;

    // --- Logique Metier ---

    /**
     * Recupere le montant actuel. 
     * Si la cagnotte a des phases, le montant est la somme des montants des phases.
     */
    public BigDecimal getCurrentAmount() {
        if (children != null && !children.isEmpty()) {
            return children.stream()
                    .map(Pool::getCurrentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return currentAmount != null ? currentAmount : BigDecimal.ZERO;
    }

    /**
     * Recupere l'objectif financier.
     * Si la cagnotte a des phases, l'objectif est la somme des objectifs des phases.
     */
    public BigDecimal getGoalAmount() {
        if (children != null && !children.isEmpty()) {
            return children.stream()
                    .map(Pool::getGoalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return goalAmount != null ? goalAmount : BigDecimal.ZERO;
    }

    /**
     * Identifie la phase active (la premiere qui n'est pas cloturee).
     */
    public java.util.Optional<Pool> getActivePhase() {
        if (children == null || children.isEmpty()) {
            return java.util.Optional.empty();
        }
        return children.stream()
                .filter(p -> !PoolStatus.CLOTUREE.equals(p.getStatus()) && !PoolStatus.ARCHIVEE.equals(p.getStatus()))
                .findFirst();
    }

    /**
     * Verifie si la cagnotte est de type Tontine.
     */
    public boolean isTontine() {
        return PoolType.PRIVATE_TONTINE.equals(this.type);
    }

    /**
     * Verifie si un utilisateur est autorise a voir ou contribuer a la cagnotte.
     */
    public boolean isUserAllowed(String userId, String email) {
        if (PoolType.PUBLIC.equals(this.type)) {
            return true;
        }
        // Pour une tontine, seuls le proprietaire et les invites sont autorises.
        boolean isInvited = (invitations != null && invitations.stream().anyMatch(i -> i.getEmail().equalsIgnoreCase(email)))
                || (invitedUserIds != null && invitedUserIds.contains(userId));
        
        return userId != null && userId.equals(ownerId) || isInvited;
    }

    /**
     * Verifie si la cagnotte peut passer en revue.
     */
    public boolean canBeSubmitted() {
        return PoolStatus.BROUILLON.equals(this.status) 
                && title != null && !title.isBlank()
                && goalAmount != null && goalAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Transitionne la cagnotte vers l'etat EN_REVUE.
     */
    public void submitForReview() {
        if (!canBeSubmitted()) {
            throw new IllegalStateException("La cagnotte n'est pas prete pour la revue.");
        }
        this.status = PoolStatus.EN_REVUE;
    }

    /**
     * Calcule le pourcentage de progression.
     */
    public BigDecimal getProgressPercentage() {
        BigDecimal goal = getGoalAmount();
        BigDecimal current = getCurrentAmount();
        if (goal == null || goal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return current.multiply(new BigDecimal("100"))
                .divide(goal, 2, RoundingMode.HALF_UP);
    }
}
