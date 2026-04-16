package com.alphateckplus.potify.data_jpa.repository.payment;

import com.alphateckplus.potify.data_jpa.entity.payment.RefundEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA des remboursements.
 */
public interface RefundEntityRepository extends JpaRepository<RefundEntity, String> {
    List<RefundEntity> findByContributionId(String contributionId);
}
