package com.alphateckplus.potify.data_jpa.entity.pool;

import com.alphateckplus.potify.data_jpa.entity.common.BaseEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "comment_reactions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"message_id", "user_id", "reaction_type"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommentReactionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private MessageEntity message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "reaction_type", nullable = false, length = 50)
    private String reactionType; // Ex: "LIKE", "HEART", "LAUGH", etc.
}
