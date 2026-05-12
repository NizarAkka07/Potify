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

    @GetMapping("/public")
    @Operation(summary = "Lister les cagnottes publiques avec recherche optionnelle")
    public ResponseEntity<List<PoolResponse>> listPublicPools(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String search) {
        List<PoolResponse> pools = listPoolsService.searchPublicPools(search).stream()
                .map(poolRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pools);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lister les cagnottes d'un utilisateur (proprietaires et invitees)")
    public ResponseEntity<List<PoolResponse>> listUserPools(
            @org.springframework.web.bind.annotation.PathVariable String userId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String email) {
        String effectiveEmail = (email != null) ? email : "";
        List<PoolResponse> pools = listPoolsService.findByUser(userId, effectiveEmail).stream()
                .map(poolRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pools);
    }

    @GetMapping("/user/{userId}/invited")
    @Operation(summary = "Lister uniquement les cagnottes auxquelles l'utilisateur a ete invite")
    public ResponseEntity<List<PoolResponse>> listInvitedPools(
            @org.springframework.web.bind.annotation.PathVariable String userId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String email) {
        String effectiveEmail = (email != null) ? email : "";
        List<PoolResponse> pools = listPoolsService.findInvitedPools(userId, effectiveEmail).stream()
                .map(poolRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pools);
    }
}
