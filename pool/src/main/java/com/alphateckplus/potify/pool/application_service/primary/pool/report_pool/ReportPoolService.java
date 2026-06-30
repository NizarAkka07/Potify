package com.alphateckplus.potify.pool.application_service.primary.pool.report_pool;

import com.alphateckplus.potify.pool.domain.model.Pool;
import java.util.List;

public interface ReportPoolService {
    void reportPool(String poolId, String userId, String reason);
    void dismissPoolReports(String poolId);
    List<Pool> getReportedPools();
}
