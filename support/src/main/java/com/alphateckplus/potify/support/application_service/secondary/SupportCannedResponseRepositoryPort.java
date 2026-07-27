package com.alphateckplus.potify.support.application_service.secondary;

import com.alphateckplus.potify.support.domain.model.SupportCannedResponse;
import java.util.List;

public interface SupportCannedResponseRepositoryPort {

    List<SupportCannedResponse> findAll();
}
