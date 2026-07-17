package com.alphateckplus.potify.pool.application_service.primary.pool.workflow;

import com.alphateckplus.potify.pool.domain.model.Pool;

public interface RejectPoolService {
    Pool execute(String id, String reason) throws Exception;
}
