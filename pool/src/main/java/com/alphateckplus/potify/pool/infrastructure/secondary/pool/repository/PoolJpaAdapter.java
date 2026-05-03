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

    public PoolJpaAdapter(
            PoolEntityRepository poolEntityRepository,
            UserEntityRepository userEntityRepository,
            PoolPersistenceMapper poolPersistenceMapper) {
        this.poolEntityRepository = poolEntityRepository;
        this.userEntityRepository = userEntityRepository;
        this.poolPersistenceMapper = poolPersistenceMapper;
    }

    @Override
    public Pool save(Pool pool) {
        PoolEntity entity = poolPersistenceMapper.toEntity(pool);

        if (pool.getOwnerId() != null) {
            UserEntity owner = userEntityRepository.findById(pool.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouve : " + pool.getOwnerId()));
            entity.setOwner(owner);
        }

        if (pool.getParentId() != null) {
            PoolEntity parent = poolEntityRepository.findById(pool.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Cagnotte parente non trouvee : " + pool.getParentId()));
            entity.setParent(parent);
        }

        PoolEntity saved = poolEntityRepository.save(entity);
        return poolPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Pool> findById(String id) {
        return poolEntityRepository.findById(id)
                .map(poolPersistenceMapper::toDomain);
    }

    @Override
    public List<Pool> findAll() {
        return poolEntityRepository.findAll().stream()
                .map(poolPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        poolEntityRepository.deleteById(id);
    }
}
