package com.alphateckplus.potify.pool.infrastructure.primary.pool.reaction;

import com.alphateckplus.potify.pool.application_service.secondary.pool.ReactionRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Reaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Unified Heart & Like Reaction Management")
public class UnifiedReactionController {

    private final ReactionRepositoryPort reactionRepositoryPort;

    @PostMapping("/reactions/toggle")
    @Operation(summary = "Basculer un Like/Cœur sur n'importe quel objet (POOL, UPDATE, MESSAGE)")
    public ResponseEntity<Map<String, Object>> toggleReaction(
            @RequestParam String targetId,
            @RequestParam(defaultValue = "UPDATE") String targetType,
            @RequestParam(required = false) String userId) {

        boolean added = reactionRepositoryPort.toggleReaction(targetId, targetType, userId);
        long count = reactionRepositoryPort.countByTarget(targetId, targetType);

        return ResponseEntity.ok(Map.of(
                "liked", added,
                "count", count,
                "targetId", targetId,
                "targetType", targetType
        ));
    }

    @PostMapping("/updates/{updateId}/like")
    @Operation(summary = "Liker / Aimer une actualité de projet (Heart)")
    public ResponseEntity<Map<String, Object>> toggleUpdateLike(
            @PathVariable String updateId,
            @RequestParam(required = false) String userId) {

        boolean added = reactionRepositoryPort.toggleReaction(updateId, "UPDATE", userId);
        long count = reactionRepositoryPort.countByTarget(updateId, "UPDATE");

        return ResponseEntity.ok(Map.of(
                "liked", added,
                "count", count,
                "updateId", updateId
        ));
    }

    @PostMapping("/pools/{poolId}/like")
    @Operation(summary = "Liker / Aimer une cagnotte (Heart)")
    public ResponseEntity<Map<String, Object>> togglePoolLike(
            @PathVariable String poolId,
            @RequestParam(required = false) String userId) {

        boolean added = reactionRepositoryPort.toggleReaction(poolId, "POOL", userId);
        long count = reactionRepositoryPort.countByTarget(poolId, "POOL");

        return ResponseEntity.ok(Map.of(
                "liked", added,
                "count", count,
                "poolId", poolId
        ));
    }

    @GetMapping("/reactions/count")
    @Operation(summary = "Obtenir le nombre de hearts d'une cible")
    public ResponseEntity<Map<String, Object>> getReactionCount(
            @RequestParam String targetId,
            @RequestParam(defaultValue = "UPDATE") String targetType) {

        long count = reactionRepositoryPort.countByTarget(targetId, targetType);
        return ResponseEntity.ok(Map.of("targetId", targetId, "targetType", targetType, "count", count));
    }

    @GetMapping("/reactions/status")
    @Operation(summary = "Obtenir le statut et le nombre de réctions d'une cible pour un utilisateur")
    public ResponseEntity<Map<String, Object>> getReactionStatus(
            @RequestParam String targetId,
            @RequestParam(defaultValue = "UPDATE") String targetType,
            @RequestParam(required = false) String userId) {

        long count = reactionRepositoryPort.countByTarget(targetId, targetType);
        boolean liked = false;
        if (userId != null && !userId.isBlank()) {
            List<Reaction> list = reactionRepositoryPort.findByTarget(targetId, targetType);
            liked = list.stream().anyMatch(r -> userId.equals(r.getUserId()));
        }
        return ResponseEntity.ok(Map.of("targetId", targetId, "targetType", targetType, "count", count, "liked", liked));
    }
}
