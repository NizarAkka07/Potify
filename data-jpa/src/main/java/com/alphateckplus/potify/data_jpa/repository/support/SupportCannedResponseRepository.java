package com.alphateckplus.potify.data_jpa.repository.support;

import com.alphateckplus.potify.data_jpa.entity.support.SupportCannedResponseEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportCannedResponseRepository extends JpaRepository<SupportCannedResponseEntity, String> {

    Optional<SupportCannedResponseEntity> findByShortcut(String shortcut);
}
