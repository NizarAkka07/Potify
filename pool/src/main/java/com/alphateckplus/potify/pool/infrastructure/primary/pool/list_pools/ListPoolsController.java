package com.alphateckplus.potify.pool.infrastructure.primary.pool.list_pools;

import com.alphateckplus.potify.pool.application_service.primary.pool.list_pools.ListPoolsService;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper.PoolRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Management")
public class ListPoolsController {

    private final ListPoolsService listPoolsService;
    private final PoolRestMapper poolRestMapper;

    @GetMapping
    @Operation(summary = "Lister toutes les cagnottes")
    public ResponseEntity<List<PoolResponse>> listPools() {
        List<PoolResponse> pools = listPoolsService.execute().stream()
                .map(poolRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pools);
    }
}
