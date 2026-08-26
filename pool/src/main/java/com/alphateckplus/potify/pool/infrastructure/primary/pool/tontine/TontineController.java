package com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine;

import com.alphateckplus.potify.data_jpa.entity.pool.TontineDetailsEntity;
import com.alphateckplus.potify.pool.application_service.tontine.TontineService;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto.ConfigureTontineRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto.TontineContributeRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto.TontineSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pools/{poolId}/tontine")
@RequiredArgsConstructor
@Tag(name = "Tontine Management")
public class TontineController {

    private final TontineService tontineService;

    @PostMapping("/config")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Configurer les paramètres d'une tontine")
    public ResponseEntity<TontineDetailsEntity> configureTontine(
            @PathVariable String poolId,
            @Valid @RequestBody ConfigureTontineRequest request) {
        TontineDetailsEntity details = tontineService.configureTontine(poolId, request);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/start")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Démarrer la tontine et générer l'ordre de passage et les tours")
    public ResponseEntity<TontineSummaryResponse> startTontine(@PathVariable String poolId) {
        TontineSummaryResponse summary = tontineService.startTontine(poolId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary")
    @Operation(summary = "Obtenir le résumé complet de la tontine (tour actif, membres, historique)")
    public ResponseEntity<TontineSummaryResponse> getTontineSummary(@PathVariable String poolId) {
        TontineSummaryResponse summary = tontineService.getTontineSummary(poolId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/contribute")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Effectuer la cotisation pour le tour actif de la tontine")
    public ResponseEntity<TontineSummaryResponse> processContribution(
            @PathVariable String poolId,
            @Valid @RequestBody TontineContributeRequest request) {
        TontineSummaryResponse response = tontineService.processTontineContribution(poolId, request.userId(), request.paymentMethod());
        return ResponseEntity.ok(response);
    }
}
