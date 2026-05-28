package com.alphateckplus.potify.data_jpa.entity.pool;

import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Entite de cagnotte (pool).
 *
 * <p>Cette classe ne contient que la structure de persistence.
 * La logique metier (regles de cloture, plafonds, etc.) doit rester dans le domaine.
 */
@Entity
@Table(name = "pools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"parent", "children", "owner"})
public class PoolEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    /**
     * Auto-reference pour supporter une hierarchie parent/enfant de cagnottes.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PoolEntity parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PoolEntity> children = new HashSet<>();

    @Column(name = "title", nullable = false, length = 140)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "goal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal goalAmount;

    @Column(name = "current_amount", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CagnotteStatus status;

    @Column(name = "has_deadline")
    @Builder.Default
    private Boolean hasDeadline = false;

    @Column(name = "deadline_date")
    private LocalDateTime deadlineDate;

    @OneToMany(mappedBy = "pool", cascade = jakarta.persistence.CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    @Builder.Default
    private List<PhaseEntity> phases = new java.util.ArrayList<>();

    @Column(name = "type", nullable = false, length = 20)
    private String type; // On stocke le type (PUBLIC, PRIVATE_TONTINE)

    /**
     * Pour la tontine, on stocke les IDs des invites.
     * Dans une version plus complexe, on utiliserait une table de jointure.
     */
    @Column(name = "invited_user_ids", length = 2000)
    private String invitedUserIds;

    @Column(name = "image_content", columnDefinition = "bytea")
    private byte[] imageContent;

    @Column(name = "image_content_type", length = 50)
    private String imageContentType;

    @Column(name = "video_content", columnDefinition = "bytea")
    private byte[] videoContent;

    @Column(name = "video_content_type", length = 50)
    private String videoContentType;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @jakarta.persistence.OneToOne(mappedBy = "pool", cascade = jakarta.persistence.CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    private CagnotteWalletEntity wallet;

    @jakarta.persistence.OneToMany(mappedBy = "pool", cascade = jakarta.persistence.CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    @lombok.Builder.Default
    private java.util.List<PoolInvitationEntity> invitations = new java.util.ArrayList<>();
}
