package com.alphateckplus.potify.data_jpa.repository.payment;

import com.alphateckplus.potify.data_jpa.entity.payment.ContributionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA des contributions.
 */
public interface ContributionEntityRepository extends JpaRepository<ContributionEntity, String> {
    List<ContributionEntity> findByPoolId(String poolId);
}
