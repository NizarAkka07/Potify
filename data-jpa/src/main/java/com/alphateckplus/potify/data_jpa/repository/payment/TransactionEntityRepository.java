package com.alphateckplus.potify.data_jpa.repository.payment;

import com.alphateckplus.potify.data_jpa.entity.payment.TransactionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA des transactions.
 */
public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, String> {
    List<TransactionEntity> findByWalletId(String walletId);
}
