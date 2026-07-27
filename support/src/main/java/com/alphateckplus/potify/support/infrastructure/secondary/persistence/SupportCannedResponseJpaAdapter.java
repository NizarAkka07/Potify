package com.alphateckplus.potify.support.infrastructure.secondary.persistence;

import com.alphateckplus.potify.data_jpa.entity.support.SupportCannedResponseEntity;
import com.alphateckplus.potify.data_jpa.repository.support.SupportCannedResponseRepository;
import com.alphateckplus.potify.support.application_service.secondary.SupportCannedResponseRepositoryPort;
import com.alphateckplus.potify.support.domain.model.SupportCannedResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupportCannedResponseJpaAdapter implements SupportCannedResponseRepositoryPort {

    private final SupportCannedResponseRepository cannedResponseRepository;

    @Override
    public List<SupportCannedResponse> findAll() {
        List<SupportCannedResponseEntity> entities = cannedResponseRepository.findAll();
        return entities.stream()
                .map(entity -> SupportCannedResponse.builder()
                        .id(entity.getId())
                        .shortcut(entity.getShortcut())
                        .title(entity.getTitle())
                        .content(entity.getContent())
                        .build())
                .toList();
    }
}
