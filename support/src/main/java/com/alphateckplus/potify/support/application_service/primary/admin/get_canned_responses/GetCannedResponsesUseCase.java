package com.alphateckplus.potify.support.application_service.primary.admin.get_canned_responses;

import com.alphateckplus.potify.data_jpa.entity.support.SupportCannedResponseEntity;
import java.util.List;

public interface GetCannedResponsesUseCase {

    List<SupportCannedResponseEntity> getCannedResponses();
}
