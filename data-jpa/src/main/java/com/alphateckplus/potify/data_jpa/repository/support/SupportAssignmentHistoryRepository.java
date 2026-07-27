package com.alphateckplus.potify.data_jpa.repository.support;

import com.alphateckplus.potify.data_jpa.entity.support.SupportAssignmentHistoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportAssignmentHistoryRepository extends JpaRepository<SupportAssignmentHistoryEntity, String> {

    List<SupportAssignmentHistoryEntity> findByConversationIdOrderByCreatedAtDesc(String conversationId);
}
