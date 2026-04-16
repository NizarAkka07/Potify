package com.alphateckplus.potify.data_jpa.repository.notification;

import com.alphateckplus.potify.data_jpa.entity.notification.NotificationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA des notifications.
 */
public interface NotificationEntityRepository extends JpaRepository<NotificationEntity, String> {
    List<NotificationEntity> findByUserId(String userId);
}
