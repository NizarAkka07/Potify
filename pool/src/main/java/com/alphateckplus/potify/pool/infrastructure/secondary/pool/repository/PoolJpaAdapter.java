package com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.PoolPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter sortant pour la persistance des cagnottes via JPA.
 */
@Transactional
public class PoolJpaAdapter implements PoolRepositoryPort {

    private final PoolEntityRepository poolEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final PoolPersistenceMapper poolPersistenceMapper;
    private final com.alphateckplus.potify.data_jpa.repository.pool.PoolReportEntityRepository poolReportEntityRepository;

    public PoolJpaAdapter(
            PoolEntityRepository poolEntityRepository,
            UserEntityRepository userEntityRepository,
            PoolPersistenceMapper poolPersistenceMapper,
            com.alphateckplus.potify.data_jpa.repository.pool.PoolReportEntityRepository poolReportEntityRepository) {
        this.poolEntityRepository = poolEntityRepository;
        this.userEntityRepository = userEntityRepository;
        this.poolPersistenceMapper = poolPersistenceMapper;
        this.poolReportEntityRepository = poolReportEntityRepository;
    }

    @Override
    public Pool save(Pool pool) {
        PoolEntity entity = poolPersistenceMapper.toEntity(pool);

        if (pool.getOwnerId() != null) {
            UserEntity owner = userEntityRepository.findById(pool.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouve : " + pool.getOwnerId()));
            entity.setOwner(owner);
        }

        if (pool.getParentId() != null && !pool.getParentId().isBlank()) {
            PoolEntity parent = poolEntityRepository.findById(pool.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Cagnotte parente non trouvee : " + pool.getParentId()));
            entity.setParent(parent);
        }

        PoolEntity saved = poolEntityRepository.save(entity);
        return poolPersistenceMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pool> findById(String id) {
        return poolEntityRepository.findById(id)
                .map(poolPersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pool> findAll() {
        return poolEntityRepository.findAll().stream()
                .map(poolPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pool> findPublicPools() {
        return poolEntityRepository.findByTypeAndParentIsNull("PUBLIC").stream()
                .map(poolPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pool> searchPublicPools(String query) {
        return poolEntityRepository.findByTypeAndTitleContainingIgnoreCaseAndParentIsNull("PUBLIC", query).stream()
                .map(poolPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pool> findByOwnerId(String ownerId) {
        return poolEntityRepository.findByOwnerIdAndParentIsNull(ownerId).stream()
                .map(poolPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        poolEntityRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pool> findByUser(String userId, String email) {
        return poolEntityRepository.findByUser(userId, email).stream()
                .map(poolPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pool> findInvitedPools(String userId, String email) {
        return poolEntityRepository.findInvitedPools(userId, email).stream()
                .map(poolPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void addReport(String poolId, String userId, String reason) {
        PoolEntity pool = poolEntityRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte non trouvee"));
        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouve"));

        if (poolReportEntityRepository.findByPoolIdAndUserId(poolId, userId).isPresent()) {
            return;
        }

        com.alphateckplus.potify.data_jpa.entity.pool.PoolReportEntity report = com.alphateckplus.potify.data_jpa.entity.pool.PoolReportEntity.builder()
                .pool(pool)
                .user(user)
                .reason(reason)
                .build();
        poolReportEntityRepository.save(report);
    }

    @Override
    public void clearReports(String poolId) {
        PoolEntity pool = poolEntityRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte non trouvee"));
        pool.getReports().clear();
        poolEntityRepository.save(pool);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pool> findReportedPools() {
        return poolEntityRepository.findReportedPools().stream()
                .map(poolPersistenceMapper::toDomain)
                .toList();
    }
}
