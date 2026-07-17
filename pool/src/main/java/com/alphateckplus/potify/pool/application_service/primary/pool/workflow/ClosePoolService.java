package com.alphateckplus.potify.pool.application_service.primary.pool.workflow;

import com.alphateckplus.potify.pool.domain.model.Pool;

public interface ClosePoolService {
    Pool execute(String id) throws Exception;
}
