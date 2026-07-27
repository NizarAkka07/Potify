package com.alphateckplus.potify.data_jpa.entity.support;

import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Réponses pré-enregistrées / raccourcis pour les agents support.
 */
@Entity
@Table(name = "support_canned_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class SupportCannedResponseEntity extends BaseEntity {

    @Column(name = "shortcut", nullable = false, unique = true, length = 50)
    private String shortcut;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
