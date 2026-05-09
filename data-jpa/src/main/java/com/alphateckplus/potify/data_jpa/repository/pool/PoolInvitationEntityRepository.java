package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.PoolInvitationEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolInvitationEntityRepository extends JpaRepository<PoolInvitationEntity, String> {
    List<PoolInvitationEntity> findByPoolId(String poolId);
    Optional<PoolInvitationEntity> findByPoolIdAndEmail(String poolId, String email);
    Optional<PoolInvitationEntity> findByToken(String token);
}
