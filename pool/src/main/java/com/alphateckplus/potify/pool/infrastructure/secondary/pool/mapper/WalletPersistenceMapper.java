package com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper;

import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteWalletEntity;
import com.alphateckplus.potify.pool.domain.model.Wallet;

public class WalletPersistenceMapper {

    public CagnotteWalletEntity toEntity(Wallet domain) {
        if (domain == null) return null;
        return CagnotteWalletEntity.builder()
                .id(domain.getId())
                .availableBalance(domain.getAvailableBalance())
                .pendingBalance(domain.getPendingBalance())
                .build();
    }

    public Wallet toDomain(CagnotteWalletEntity entity) {
        if (entity == null) return null;
        return Wallet.builder()
                .id(entity.getId())
                .poolId(entity.getPool() != null ? entity.getPool().getId() : null)
                .availableBalance(entity.getAvailableBalance())
                .pendingBalance(entity.getPendingBalance())
                .build();
    }
}
