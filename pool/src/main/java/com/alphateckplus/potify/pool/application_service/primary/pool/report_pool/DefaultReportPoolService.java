package com.alphateckplus.potify.pool.application_service.primary.pool.report_pool;

import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Pool;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class DefaultReportPoolService implements ReportPoolService {

    private final PoolRepositoryPort poolRepositoryPort;

    @Override
    public void reportPool(String poolId, String userId, String reason) {
        poolRepositoryPort.addReport(poolId, userId, reason);
    }

    @Override
    public void dismissPoolReports(String poolId) {
        poolRepositoryPort.clearReports(poolId);
    }

    @Override
    public List<Pool> getReportedPools() {
        return poolRepositoryPort.findReportedPools();
    }
}
