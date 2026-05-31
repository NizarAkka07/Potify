package com.alphateckplus.potify.pool.infrastructure.primary.pool.list_pools.list_invited_pools;

import com.alphateckplus.potify.pool.application_service.primary.pool.list_pools.ListPoolsService;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper.PoolRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Management")
public class ListInvitedPoolsController {

    private final ListPoolsService listPoolsService;
    private final PoolRestMapper poolRestMapper;

    @GetMapping("/user/{userId}/invited")
    @Operation(summary = "Lister uniquement les cagnottes auxquelles l'utilisateur a ete invite")
    public ResponseEntity<List<PoolResponse>> listInvitedPools(
            @PathVariable String userId,
            @RequestParam(required = false) String email) {
        String effectiveEmail = (email != null) ? email : "";
        List<PoolResponse> pools = listPoolsService.findInvitedPools(userId, effectiveEmail).stream()
                .map(poolRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pools);
    }
}
