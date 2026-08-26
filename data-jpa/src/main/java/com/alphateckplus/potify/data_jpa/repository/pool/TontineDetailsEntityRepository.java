package com.alphateckplus.potify.data_jpa.repository.pool;

import com.alphateckplus.potify.data_jpa.entity.pool.TontineDetailsEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.TontineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TontineDetailsEntityRepository extends JpaRepository<TontineDetailsEntity, String> {
    Optional<TontineDetailsEntity> findByPoolId(String poolId);
    List<TontineDetailsEntity> findByStatus(TontineStatus status);
}
