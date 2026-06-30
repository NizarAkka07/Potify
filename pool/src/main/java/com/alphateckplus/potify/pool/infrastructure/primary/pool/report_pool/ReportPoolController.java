package com.alphateckplus.potify.pool.infrastructure.primary.pool.report_pool;

import com.alphateckplus.potify.pool.application_service.primary.pool.report_pool.ReportPoolService;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper.PoolRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Report Management")
public class ReportPoolController {

    private final ReportPoolService reportPoolService;
    private final PoolRestMapper poolRestMapper;

    @PostMapping("/{poolId}/report")
    @Operation(summary = "Signaler une cagnotte")
    public ResponseEntity<Void> reportPool(
            @PathVariable String poolId,
            @RequestParam String userId,
            @RequestParam(required = false, defaultValue = "Contenu inapproprié") String reason) {
        reportPoolService.reportPool(poolId, userId, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{poolId}/dismiss-reports")
    @Operation(summary = "Rejeter les signalements d'une cagnotte")
    public ResponseEntity<Void> dismissPoolReports(@PathVariable String poolId) {
        reportPoolService.dismissPoolReports(poolId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reported")
    @Operation(summary = "Lister les cagnottes signalées")
    public ResponseEntity<List<PoolResponse>> getReportedPools() {
        List<PoolResponse> pools = reportPoolService.getReportedPools().stream()
                .map(poolRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pools);
    }
}
