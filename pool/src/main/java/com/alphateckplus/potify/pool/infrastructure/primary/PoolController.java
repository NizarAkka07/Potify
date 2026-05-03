package com.alphateckplus.potify.pool.infrastructure.primary;

import com.alphateckplus.potify.pool.application_service.primary.pool.create_pool.CreatePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.get_pool.GetPoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.list_pools.ListPoolsService;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.infrastructure.primary.dto.CreatePoolRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.dto.PoolResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controleur REST pour la gestion des cagnottes.
 */
@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Management", description = "Endpoints pour la gestion des cagnottes")
public class PoolController {

    private final CreatePoolService createPoolService;
    private final GetPoolService getPoolService;
    private final ListPoolsService listPoolsService;
    private final PoolRestMapper poolRestMapper;

    @PostMapping
    @Operation(summary = "Creer une nouvelle cagnotte")
    public ResponseEntity<PoolResponse> createPool(@Valid @RequestBody CreatePoolRequest request) {
        Pool createdPool = createPoolService.execute(poolRestMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(poolRestMapper.toResponse(createdPool));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer une cagnotte par son ID")
    public ResponseEntity<PoolResponse> getPool(@PathVariable String id) {
        return getPoolService.execute(id)
                .map(poolRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Lister toutes les cagnottes")
    public ResponseEntity<List<PoolResponse>> listPools() {
        List<PoolResponse> pools = listPoolsService.execute().stream()
                .map(poolRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pools);
    }
}
