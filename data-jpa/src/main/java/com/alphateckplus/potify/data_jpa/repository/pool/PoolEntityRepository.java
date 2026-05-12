package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository JPA des cagnottes.
 */
public interface PoolEntityRepository extends JpaRepository<PoolEntity, String> {
    List<PoolEntity> findByOwnerId(String ownerId);
    List<PoolEntity> findByType(String type);
    List<PoolEntity> findByTypeAndTitleContainingIgnoreCase(String type, String title);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM PoolEntity p WHERE p.owner.id = :userId OR p.id IN (SELECT i.pool.id FROM PoolInvitationEntity i WHERE LOWER(i.email) = LOWER(:email) AND i.status = 'ACCEPTED')")
    List<PoolEntity> findByUser(@org.springframework.data.repository.query.Param("userId") String userId, @org.springframework.data.repository.query.Param("email") String email);
}
