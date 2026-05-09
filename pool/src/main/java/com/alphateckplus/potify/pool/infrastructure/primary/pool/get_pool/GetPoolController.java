package com.alphateckplus.potify.pool.infrastructure.primary.pool.get_pool;

import com.alphateckplus.potify.pool.application_service.primary.pool.get_pool.GetPoolService;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper.PoolRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Management")
public class GetPoolController {

    private final GetPoolService getPoolService;
    private final PoolRestMapper poolRestMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer une cagnotte par son ID")
    public ResponseEntity<PoolResponse> getPool(
            @PathVariable String id,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String userId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String email) {
        return getPoolService.execute(id, userId, email)
                .map(poolRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }
}
