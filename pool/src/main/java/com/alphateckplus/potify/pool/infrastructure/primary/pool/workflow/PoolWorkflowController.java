package com.alphateckplus.potify.pool.infrastructure.primary.pool.workflow;

import com.alphateckplus.potify.pool.application_service.primary.pool.workflow.*;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper.PoolRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Workflow Management")
public class PoolWorkflowController {

    private final PublishPoolService publishPoolService;
    private final ApprovePoolService approvePoolService;
    private final RejectPoolService rejectPoolService;
    private final SuspendPoolService suspendPoolService;
    private final ClosePoolService closePoolService;
    private final PoolRestMapper poolRestMapper;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReasonRequest {
        private String reason;
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Soumettre une cagnotte à la revue administrative")
    public ResponseEntity<PoolResponse> submit(@PathVariable String id) throws Exception {
        Pool pool = publishPoolService.execute(id);
        return ResponseEntity.ok(poolRestMapper.toResponse(pool));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('POOL_MODERATE') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MODERATEUR') or hasAuthority('ROLE_ADMIN_POOL')")
    @Operation(summary = "Approuver et publier une cagnotte")
    public ResponseEntity<PoolResponse> approve(@PathVariable String id) throws Exception {
        Pool pool = approvePoolService.execute(id);
        return ResponseEntity.ok(poolRestMapper.toResponse(pool));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('POOL_MODERATE') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MODERATEUR') or hasAuthority('ROLE_ADMIN_POOL')")
    @Operation(summary = "Rejeter une cagnotte en revue")
    public ResponseEntity<PoolResponse> reject(@PathVariable String id, @RequestBody ReasonRequest request) throws Exception {
        Pool pool = rejectPoolService.execute(id, request.getReason() != null ? request.getReason() : "Non spécifié");
        return ResponseEntity.ok(poolRestMapper.toResponse(pool));
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasAuthority('POOL_MODERATE') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MODERATEUR') or hasAuthority('ROLE_ADMIN_POOL')")
    @Operation(summary = "Suspendre temporairement une cagnotte active")
    public ResponseEntity<PoolResponse> suspend(@PathVariable String id, @RequestBody ReasonRequest request) throws Exception {
        Pool pool = suspendPoolService.execute(id, request.getReason() != null ? request.getReason() : "Non spécifié");
        return ResponseEntity.ok(poolRestMapper.toResponse(pool));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Clôturer une cagnotte")
    public ResponseEntity<PoolResponse> close(@PathVariable String id) throws Exception {
        Pool pool = closePoolService.execute(id);
        return ResponseEntity.ok(poolRestMapper.toResponse(pool));
    }
}
