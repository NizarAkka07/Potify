package com.alphateckplus.potify.pool.infrastructure.primary.pool.increment_views;

import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour incrementer le compteur de vues d'une cagnotte (public pour tous les visiteurs).
 */
@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Management")
public class IncrementPoolViewsController {

    private final PoolRepositoryPort poolRepositoryPort;

    @PostMapping("/{id}/view")
    @Operation(summary = "Incrementer le compteur de vues d'une cagnotte")
    public ResponseEntity<Void> incrementViews(@PathVariable String id) {
        poolRepositoryPort.incrementViewsCount(id);
        return ResponseEntity.ok().build();
    }
}
