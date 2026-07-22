package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository JPA des cagnottes.
 */
public interface PoolEntityRepository extends JpaRepository<PoolEntity, String> {
    List<PoolEntity> findByOwnerIdAndParentIsNull(String ownerId);
    List<PoolEntity> findByTypeAndParentIsNull(String type);
    List<PoolEntity> findByTypeAndTitleContainingIgnoreCaseAndParentIsNull(String type, String title);

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT DISTINCT p.* FROM pools p WHERE (p.owner_id = :userId OR p.id IN (SELECT i.pool_id FROM pool_invitations i WHERE LOWER(i.email) = LOWER(:email) AND i.status IN ('ACCEPTED', 'PENDING'))) AND p.parent_id IS NULL",
        nativeQuery = true
    )
    List<PoolEntity> findByUser(@org.springframework.data.repository.query.Param("userId") String userId, @org.springframework.data.repository.query.Param("email") String email);

    /** Récupère les cagnottes auxquelles l'utilisateur a été invité (excluant celles dont il est propriétaire). */
    @org.springframework.data.jpa.repository.Query(
        value = "SELECT DISTINCT p.* FROM pools p WHERE p.owner_id <> :userId AND p.id IN (SELECT i.pool_id FROM pool_invitations i WHERE LOWER(i.email) = LOWER(:email) AND i.status IN ('ACCEPTED', 'PENDING')) AND p.parent_id IS NULL",
        nativeQuery = true
    )
    List<PoolEntity> findInvitedPools(@org.springframework.data.repository.query.Param("userId") String userId, @org.springframework.data.repository.query.Param("email") String email);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p FROM PoolEntity p JOIN p.reports r ORDER BY p.createdAt DESC")
    List<PoolEntity> findReportedPools();

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE PoolEntity p SET p.viewsCount = COALESCE(p.viewsCount, 0) + 1 WHERE p.id = :id")
    void incrementViewsCount(@org.springframework.data.repository.query.Param("id") String id);
}
