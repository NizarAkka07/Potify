package com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository;

import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteWalletEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.repository.pool.CagnotteWalletEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.pool.application_service.secondary.pool.WalletRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Wallet;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.WalletPersistenceMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class WalletJpaAdapter implements WalletRepositoryPort {

    private final CagnotteWalletEntityRepository walletRepository;
    private final PoolEntityRepository poolRepository;
    private final WalletPersistenceMapper mapper;

    @Override
    public Wallet save(Wallet wallet) {
        CagnotteWalletEntity entity = mapper.toEntity(wallet);
        
        PoolEntity pool = poolRepository.findById(wallet.getPoolId())
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte non trouvee"));
        
        entity.setPool(pool);
        
        return mapper.toDomain(walletRepository.save(entity));
    }

    @Override
    public Optional<Wallet> findByPoolId(String poolId) {
        return walletRepository.findByPoolId(poolId).map(mapper::toDomain);
    }
}
