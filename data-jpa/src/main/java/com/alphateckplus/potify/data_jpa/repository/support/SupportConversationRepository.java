package com.alphateckplus.potify.data_jpa.repository.support;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.data_jpa.entity.support.SupportConversationEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportConversationRepository extends JpaRepository<SupportConversationEntity, String> {

    Optional<SupportConversationEntity> findFirstByUserIdAndStatusInOrderByCreatedAtDesc(
            String userId, List<ConversationStatus> statuses);

    Page<SupportConversationEntity> findByStatus(ConversationStatus status, Pageable pageable);

    Page<SupportConversationEntity> findByAssignedAdminIdAndStatus(
            String assignedAdminId, ConversationStatus status, Pageable pageable);

    Page<SupportConversationEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    @Query("SELECT c FROM SupportConversationEntity c WHERE c.status IN :statuses")
    List<SupportConversationEntity> findAllByStatusIn(@Param("statuses") List<ConversationStatus> statuses);
}
