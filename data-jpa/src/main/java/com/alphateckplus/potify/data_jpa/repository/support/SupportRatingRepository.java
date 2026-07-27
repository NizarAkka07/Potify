package com.alphateckplus.potify.data_jpa.repository.support;

import com.alphateckplus.potify.data_jpa.entity.support.SupportRatingEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRatingRepository extends JpaRepository<SupportRatingEntity, String> {

    Optional<SupportRatingEntity> findByConversationId(String conversationId);
}
