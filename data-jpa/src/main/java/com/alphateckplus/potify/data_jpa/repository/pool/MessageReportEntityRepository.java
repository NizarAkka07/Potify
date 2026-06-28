package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.MessageReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MessageReportEntityRepository extends JpaRepository<MessageReportEntity, String> {
    List<MessageReportEntity> findByMessageId(String messageId);
    Optional<MessageReportEntity> findByMessageIdAndUserId(String messageId, String userId);
}
