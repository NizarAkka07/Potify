package com.alphateckplus.potify.pool.application_service.primary.pool.workflow;

import com.alphateckplus.potify.pool.domain.model.Pool;

public interface ApprovePoolService {
    Pool execute(String id) throws Exception;
    Pool execute(String id, String customTitle, String customMessage) throws Exception;
}
