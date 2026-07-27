package com.alphateckplus.potify.data_jpa.repository.support;

import com.alphateckplus.potify.data_jpa.entity.support.MessageStatus;
import com.alphateckplus.potify.data_jpa.entity.support.SupportMessageEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportMessageRepository extends JpaRepository<SupportMessageEntity, String> {

    Page<SupportMessageEntity> findByConversationIdOrderByCreatedAtDesc(String conversationId, Pageable pageable);

    List<SupportMessageEntity> findByConversationIdOrderByCreatedAtAsc(String conversationId);

    @Modifying
    @Query("UPDATE SupportMessageEntity m SET m.status = :status WHERE m.conversation.id = :conversationId AND m.status <> :status")
    void markAllMessagesAsRead(@Param("conversationId") String conversationId, @Param("status") MessageStatus status);
}
