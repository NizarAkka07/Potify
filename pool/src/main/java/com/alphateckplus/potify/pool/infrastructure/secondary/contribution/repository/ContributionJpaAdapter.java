package com.alphateckplus.potify.pool.infrastructure.secondary.contribution.repository;

import com.alphateckplus.potify.data_jpa.entity.payment.ContributionEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.payment.ContributionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.pool.application_service.secondary.contribution.ContributionRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Contribution;
import com.alphateckplus.potify.pool.infrastructure.secondary.contribution.mapper.ContributionPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter sortant pour la persistance des contributions via JPA.
 * Utilise l'entite et le repository partages avec le microservice payment.
 */
@Transactional
public class ContributionJpaAdapter implements ContributionRepositoryPort {

    private final ContributionEntityRepository contributionEntityRepository;
    private final PoolEntityRepository poolEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final ContributionPersistenceMapper mapper;

    public ContributionJpaAdapter(
            ContributionEntityRepository contributionEntityRepository,
            PoolEntityRepository poolEntityRepository,
            UserEntityRepository userEntityRepository,
            ContributionPersistenceMapper mapper) {
        this.contributionEntityRepository = contributionEntityRepository;
        this.poolEntityRepository = poolEntityRepository;
        this.userEntityRepository = userEntityRepository;
        this.mapper = mapper;
    }

    @Override
    public Contribution save(Contribution contribution) {
        ContributionEntity entity = mapper.toEntity(contribution);

        if (contribution.getPoolId() != null) {
            PoolEntity pool = poolEntityRepository.findById(contribution.getPoolId())
                    .orElseThrow(() -> new IllegalArgumentException("Cagnotte non trouvee : " + contribution.getPoolId()));
            entity.setPool(pool);
        }

        if (contribution.getUserId() != null) {
            UserEntity user = userEntityRepository.findById(contribution.getUserId()).orElse(null);
            entity.setUser(user);
        }

        ContributionEntity saved = contributionEntityRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Contribution> findById(String id) {
        return contributionEntityRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Contribution> findByPoolId(String poolId) {
        return contributionEntityRepository.findByPoolId(poolId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Contribution> findByUserId(String userId) {
        return contributionEntityRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
