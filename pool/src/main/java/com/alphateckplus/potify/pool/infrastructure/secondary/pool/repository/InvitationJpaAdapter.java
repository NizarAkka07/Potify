package com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolInvitationEntity;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolInvitationEntityRepository;
import com.alphateckplus.potify.pool.application_service.secondary.pool.InvitationRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Invitation;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.InvitationPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InvitationJpaAdapter implements InvitationRepositoryPort {

    private final PoolInvitationEntityRepository invitationRepository;
    private final PoolEntityRepository poolRepository;
    private final InvitationPersistenceMapper mapper;

    @Override
    public Invitation save(Invitation invitation) {
        PoolInvitationEntity entity = mapper.toEntity(invitation);
        PoolEntity pool = poolRepository.findById(invitation.getPoolId())
                .orElseThrow(() -> new IllegalArgumentException("Pool not found"));
        entity.setPool(pool);
        return mapper.toDomain(invitationRepository.save(entity));
    }

    @Override
    public List<Invitation> findByPoolId(String poolId) {
        return invitationRepository.findByPoolId(poolId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Invitation> findByPoolIdAndEmail(String poolId, String email) {
        return invitationRepository.findByPoolIdAndEmail(poolId, email).map(mapper::toDomain);
    }

    @Override
    public Optional<Invitation> findByToken(String token) {
        return invitationRepository.findByToken(token).map(mapper::toDomain);
    }
}
