package com.alphateckplus.potify.data_jpa.entity.support;

import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Historique des assignations et transferts de tickets de support.
 */
@Entity
@Table(name = "support_assignment_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"conversation", "previousAdmin", "newAdmin", "assignedBy"})
public class SupportAssignmentHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private SupportConversationEntity conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_admin_id")
    private UserEntity previousAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_admin_id")
    private UserEntity newAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_id")
    private UserEntity assignedBy;

    @Column(name = "reason", length = 255)
    private String reason;
}
