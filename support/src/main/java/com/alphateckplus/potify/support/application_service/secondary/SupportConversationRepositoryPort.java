package com.alphateckplus.potify.support.application_service.secondary;

import com.alphateckplus.potify.data_jpa.entity.support.ConversationStatus;
import com.alphateckplus.potify.support.domain.model.SupportConversation;
import java.util.List;
import java.util.Optional;

public interface SupportConversationRepositoryPort {

    Optional<SupportConversation> findFirstByUserIdAndStatusIn(String userId, List<ConversationStatus> statuses);

    Optional<SupportConversation> findById(String id);

    List<SupportConversation> findAllByStatusIn(List<ConversationStatus> statuses);

    List<SupportConversation> findByAssignedAdminIdAndStatus(String adminId, ConversationStatus status, int page, int size);

    SupportConversation save(SupportConversation conversation);

    SupportConversation createNewConversation(String userEmail);
}
