package com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolUpdateEntity;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolUpdateEntityRepository;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolUpdateRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.PoolUpdate;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.PoolUpdatePersistenceMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adaptateur sortant JPA pour la gestion et la persistance des actualités de cagnottes.
 */
@RequiredArgsConstructor
@Transactional
public class PoolUpdateJpaAdapter implements PoolUpdateRepositoryPort {

    private final PoolUpdateEntityRepository poolUpdateEntityRepository;
    private final PoolEntityRepository poolEntityRepository;
    private final PoolUpdatePersistenceMapper poolUpdatePersistenceMapper;
    private final com.alphateckplus.potify.data_jpa.repository.payment.ContributionEntityRepository contributionEntityRepository;

    @Override
    public PoolUpdate save(PoolUpdate poolUpdate) {
        PoolUpdateEntity entity = poolUpdatePersistenceMapper.toEntity(poolUpdate);

        if (poolUpdate.getPoolId() != null) {
            PoolEntity pool = poolEntityRepository.findById(poolUpdate.getPoolId())
                    .orElseThrow(() -> new IllegalArgumentException("Cagnotte non trouvée : " + poolUpdate.getPoolId()));
            entity.setPool(pool);
        }

        PoolUpdateEntity saved = poolUpdateEntityRepository.save(entity);
        return poolUpdatePersistenceMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PoolUpdate> findByPoolId(String poolId) {
        return poolUpdateEntityRepository.findByPoolIdOrderByCreatedAtDesc(poolId).stream()
                .map(poolUpdatePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.alphateckplus.potify.pool.domain.model.ContributorInfo> findContributorsByPoolId(String poolId) {
        return contributionEntityRepository.findByPoolId(poolId).stream()
                .filter(c -> c.getStatus() == com.alphateckplus.potify.data_jpa.entity.payment.ContributionStatus.CONFIRMED)
                .map(c -> com.alphateckplus.potify.pool.domain.model.ContributorInfo.builder()
                        .userId(c.getUser() != null ? c.getUser().getId() : null)
                        .email(c.getContributorEmail())
                        .build())
                .filter(c -> c.getUserId() != null || c.getEmail() != null)
                .distinct()
                .toList();
    }
}
