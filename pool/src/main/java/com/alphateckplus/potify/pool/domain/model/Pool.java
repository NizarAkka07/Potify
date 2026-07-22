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

    /** Contenu binaire de la vidéo (stockage DB). */
    private byte[] videoContent;

    /** Type MIME de la vidéo (ex: video/mp4). */
    private String videoContentType;

    /** URL d'une vidéo de présentation (YouTube, Vimeo, etc.). */
    private String videoUrl;

    /** URL d'une image de présentation hébergée sur Cloudinary. */
    private String imageUrl;

    /** Portefeuille financier de la cagnotte. */
    private Wallet wallet;

    /** Liste des cagnottes filles (sous-cagnottes). */
    @Builder.Default
    private java.util.List<Pool> children = new java.util.ArrayList<>();

    /** Liste des phases de la sous-cagnotte. */
    @Builder.Default
    private java.util.List<Phase> phases = new java.util.ArrayList<>();

    /** Liste des invitations pour cette cagnotte. */
    private java.util.List<Invitation> invitations;

    /** Indique si la cagnotte/sous-cagnotte a une deadline. */
    @Builder.Default
    private Boolean hasDeadline = false;

    /** Date limite si applicable. */
    private java.time.LocalDateTime deadlineDate;

    /** Frais applicables (en pourcentage, ex: 2.00 pour 2%). */
    @Builder.Default
    private BigDecimal fees = new BigDecimal("2.00");

    @Builder.Default
    private Long viewsCount = 0L;

    private java.util.List<PoolReport> reports;

    public boolean isReported() {
        return reports != null && !reports.isEmpty();
    }

    public int getReportCount() {
        return reports != null ? reports.size() : 0;
    }

    public String getReportReason() {
        if (reports == null || reports.isEmpty()) return null;
        return reports.stream()
                .map(PoolReport::getReason)
                .filter(r -> r != null && !r.isBlank())
                .collect(java.util.stream.Collectors.joining(" | "));
    }

    // --- Logique Metier ---

    /**
     * Recupere le montant actuel. 
     * Si c'est une cagnotte principale, c'est la somme des montants des sous-cagnottes.
     * Si c'est une sous-cagnotte, c'est le currentAmount.
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
     * Si c'est une cagnotte principale, l'objectif est la somme des objectifs des sous-cagnottes.
     * Si c'est une sous-cagnotte, l'objectif est la somme des objectifs des phases.
     */
    public BigDecimal getGoalAmount() {
        if (children != null && !children.isEmpty()) {
            return children.stream()
                    .map(Pool::getGoalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        if (phases != null && !phases.isEmpty()) {
            return phases.stream()
                    .map(Phase::getGoalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return goalAmount != null ? goalAmount : BigDecimal.ZERO;
    }

    /**
     * Identifie la phase active (la premiere qui n'est pas terminee).
     */
    public java.util.Optional<Phase> getActivePhase() {
        if (phases == null || phases.isEmpty()) {
            return java.util.Optional.empty();
        }
        return phases.stream()
                .filter(p -> !PhaseStatus.COMPLETED.equals(p.getStatus()))
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



    public void addContributionAmount(BigDecimal amount) {
        if (this.parentId != null) {
            this.currentAmount = (this.currentAmount != null ? this.currentAmount : BigDecimal.ZERO).add(amount);
            updatePhasesAndStatus();
        } else if (this.children == null || this.children.isEmpty()) {
            this.currentAmount = (this.currentAmount != null ? this.currentAmount : BigDecimal.ZERO).add(amount);
            updatePhasesAndStatus();
        } else {
            throw new IllegalStateException("Cette cagnotte contient des sous-cagnottes. Les contributions doivent être ciblées sur les sous-cagnottes.");
        }
    }

    public void updatePhasesAndStatus() {
        if (phases == null || phases.isEmpty()) {
            return;
        }
        BigDecimal tempAmount = this.currentAmount != null ? this.currentAmount : BigDecimal.ZERO;
        for (Phase phase : phases) {
            if (tempAmount.compareTo(phase.getGoalAmount()) >= 0) {
                phase.setStatus(PhaseStatus.COMPLETED);
                tempAmount = tempAmount.subtract(phase.getGoalAmount());
            } else {
                phase.setStatus(PhaseStatus.ACTIVE);
            }
        }
    }

    /**
     * Calcule le pourcentage de progression.
     */
    public BigDecimal getProgressPercentage() {
        BigDecimal goal = getGoalAmount();
        BigDecimal current = getCurrentAmount() != null ? getCurrentAmount() : BigDecimal.ZERO;
        if (goal == null || goal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return current.multiply(new BigDecimal("100"))
                .divide(goal, 2, RoundingMode.HALF_UP);
    }
}
