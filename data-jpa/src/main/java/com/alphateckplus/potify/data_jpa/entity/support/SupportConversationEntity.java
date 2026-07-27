package com.alphateckplus.potify.data_jpa.entity.support;

import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Entité de persistance pour une conversation de support (IA / Admin).
 */
@Entity
@Table(name = "support_conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"user", "assignedAdmin"})
public class SupportConversationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private UserEntity assignedAdmin;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ConversationStatus status = ConversationStatus.BOT_ACTIVE;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "bot_context_json", columnDefinition = "TEXT")
    private String botContextJson;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;
}
