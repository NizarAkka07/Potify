package com.alphateckplus.potify.data_jpa.repository.payment;

import com.alphateckplus.potify.data_jpa.entity.payment.ContributionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository JPA des contributions.
 */
public interface ContributionEntityRepository extends JpaRepository<ContributionEntity, String> {
    
    @Query("SELECT c FROM ContributionEntity c WHERE c.pool.id = :poolId")
    List<ContributionEntity> findByPoolId(@Param("poolId") String poolId);
    
    @Query("SELECT c FROM ContributionEntity c WHERE c.user.id = :userId")
    List<ContributionEntity> findByUserId(@Param("userId") String userId);
}
