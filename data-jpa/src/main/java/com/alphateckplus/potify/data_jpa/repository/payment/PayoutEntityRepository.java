package com.alphateckplus.potify.data_jpa.repository.payment;

import com.alphateckplus.potify.data_jpa.entity.payment.PayoutEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA des decaissements.
 */
public interface PayoutEntityRepository extends JpaRepository<PayoutEntity, String> {
    List<PayoutEntity> findByWalletId(String walletId);
}
