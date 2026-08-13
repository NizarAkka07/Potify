package com.alphateckplus.potify.pool.application_service.primary.pool.workflow;

import com.alphateckplus.potify.pool.domain.model.Pool;

public interface SuspendPoolService {
    Pool execute(String id, String reason) throws Exception;
    Pool execute(String id, String title, String reason, String message) throws Exception;
}
