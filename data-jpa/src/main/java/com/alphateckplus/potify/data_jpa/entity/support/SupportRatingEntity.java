package com.alphateckplus.potify.data_jpa.entity.support;

import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Évaluation de la satisfaction client (CSAT) pour une conversation résolue.
 */
@Entity
@Table(name = "support_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"conversation", "user"})
public class SupportRatingEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false, unique = true)
    private SupportConversationEntity conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
}
