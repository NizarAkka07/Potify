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

    @Query("SELECT c FROM ContributionEntity c WHERE LOWER(c.contributorEmail) = LOWER(:email)")
    List<ContributionEntity> findByContributorEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ContributionEntity c WHERE c.pool.id = :poolId AND (c.user.id = :userId OR (c.contributorEmail IS NOT NULL AND LOWER(c.contributorEmail) = LOWER(:email))) AND (c.roundNumber = :roundNumber OR (c.roundNumber IS NULL AND :roundNumber = 1))")
    boolean existsByPoolIdAndUserIdOrEmailAndRoundNumber(@Param("poolId") String poolId, @Param("userId") String userId, @Param("email") String email, @Param("roundNumber") Integer roundNumber);

    @Query("SELECT c FROM ContributionEntity c WHERE c.pool.id = :poolId AND (c.roundNumber = :roundNumber OR (c.roundNumber IS NULL AND :roundNumber = 1))")
    List<ContributionEntity> findByPoolIdAndRoundNumber(@Param("poolId") String poolId, @Param("roundNumber") Integer roundNumber);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM ContributionEntity c WHERE c.pool.id = :poolId AND (c.roundNumber = :roundNumber OR (c.roundNumber IS NULL AND :roundNumber = 1)) AND c.status = 'CONFIRMED'")
    java.math.BigDecimal sumAmountByPoolIdAndRoundNumber(@Param("poolId") String poolId, @Param("roundNumber") Integer roundNumber);

    @Query("SELECT COALESCE(SUM(c.penaltyAmount), 0) FROM ContributionEntity c WHERE c.pool.id = :poolId AND (c.roundNumber = :roundNumber OR (c.roundNumber IS NULL AND :roundNumber = 1)) AND c.status = 'CONFIRMED'")
    java.math.BigDecimal sumPenaltyByPoolIdAndRoundNumber(@Param("poolId") String poolId, @Param("roundNumber") Integer roundNumber);

    @Query("SELECT COUNT(c) FROM ContributionEntity c WHERE c.pool.id = :poolId AND (c.roundNumber = :roundNumber OR (c.roundNumber IS NULL AND :roundNumber = 1)) AND c.status = 'CONFIRMED'")
    long countConfirmedByPoolIdAndRoundNumber(@Param("poolId") String poolId, @Param("roundNumber") Integer roundNumber);
}
