package com.alphateckplus.potify.user.domain.model;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Agregat metier User de l'architecture hexagonale.
 *
 * <p>Cette classe reste volontairement simple et ne depend ni de JPA,
 * ni de Spring, pour garder un coeur metier testable et faiblement couple.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** Identifiant technique unique. */
    private String id;

    /** Nom complet affiche dans l'application. */
    private String fullName;

    /** Adresse email utilisee comme identifiant fonctionnel. */
    private String email;

    /** Mot de passe en clair pour l'instant (a hasher dans une etape suivante). */
    private String password;

    /** Statut metier de l'utilisateur. */
    private UserStatus status;

    /** Horodatage de creation renseigne par la persistence. */
    private Instant createdAt;

    /** Horodatage de derniere modification renseigne par la persistence. */
    private Instant updatedAt;

    private boolean enabled;
    private boolean accountNonLocked;
    private int failedAttempts;
    private LocalDateTime lockTime;
    private String verificationToken;
    private String avatarUrl;

    /**
     * Logique metier: Verifie si le verrouillage a expire.
     */
    public boolean isLockExpired() {
        if (lockTime == null) return true;
        return lockTime.plusMinutes(30).isBefore(LocalDateTime.now());
    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
        if (this.failedAttempts >= 5) {
            this.accountNonLocked = false;
            this.lockTime = LocalDateTime.now();
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.accountNonLocked = true;
        this.lockTime = null;
    }
}
