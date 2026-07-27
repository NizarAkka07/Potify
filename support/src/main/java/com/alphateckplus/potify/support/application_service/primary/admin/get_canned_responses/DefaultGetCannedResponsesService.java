package com.alphateckplus.potify.support.application_service.primary.admin.get_canned_responses;

import com.alphateckplus.potify.data_jpa.entity.support.SupportCannedResponseEntity;
import com.alphateckplus.potify.support.application_service.secondary.SupportCannedResponseRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultGetCannedResponsesService implements GetCannedResponsesUseCase {

    private final SupportCannedResponseRepositoryPort cannedResponseRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<SupportCannedResponseEntity> getCannedResponses() {
        return cannedResponseRepositoryPort.findAll().stream()
                .map(r -> {
                    SupportCannedResponseEntity entity = new SupportCannedResponseEntity();
                    entity.setId(r.getId());
                    entity.setShortcut(r.getShortcut());
                    entity.setTitle(r.getTitle());
                    entity.setContent(r.getContent());
                    return entity;
                })
                .toList();
    }
}
