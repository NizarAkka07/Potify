package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageEntityRepository extends JpaRepository<MessageEntity, String> {
    List<MessageEntity> findByPoolIdOrderByCreatedAtDesc(String poolId);
}
