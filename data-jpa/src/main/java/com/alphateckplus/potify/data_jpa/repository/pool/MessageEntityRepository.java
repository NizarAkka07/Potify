package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.MessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA des messages de cagnotte.
 */
public interface MessageEntityRepository extends JpaRepository<MessageEntity, String> {
    List<MessageEntity> findByPoolId(String poolId);
}
