package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteWalletEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA des wallets de cagnottes.
 */
public interface CagnotteWalletEntityRepository extends JpaRepository<CagnotteWalletEntity, String> {
    Optional<CagnotteWalletEntity> findByPoolId(String poolId);
}
