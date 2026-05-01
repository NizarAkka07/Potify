package com.alphateckplus.potify.data_jpa.repository.user;

import com.alphateckplus.potify.data_jpa.entity.user.SecurityTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityTokenEntityRepository extends JpaRepository<SecurityTokenEntity, String> {
    Optional<SecurityTokenEntity> findByToken(String token);
}
