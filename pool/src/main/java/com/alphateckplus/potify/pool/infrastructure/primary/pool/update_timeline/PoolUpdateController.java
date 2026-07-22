package com.alphateckplus.potify.pool.infrastructure.primary.pool.update_timeline;

import com.alphateckplus.potify.pool.application_service.primary.pool.update_timeline.PoolUpdateService;
import com.alphateckplus.potify.pool.application_service.secondary.pool.ReactionRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.UserCheckPort;
import com.alphateckplus.potify.pool.domain.model.PoolUpdate;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolUpdateRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolUpdateResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper.PoolUpdateRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion et l'affichage des actualités de cagnotte (timeline).
 */
@RestController
@RequestMapping("/api/pools/{poolId}/updates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pool Updates & Timeline of Trust")
public class PoolUpdateController {

    private final PoolUpdateService poolUpdateService;
    private final PoolUpdateRestMapper poolUpdateRestMapper;
    private final UserCheckPort userCheckPort;
    private final ReactionRepositoryPort reactionRepositoryPort;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Publier une actualité sur la cagnotte (réservé au créateur)")
    public ResponseEntity<PoolUpdateResponse> createUpdate(
            @PathVariable String poolId,
            @Valid @RequestBody PoolUpdateRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();
        String currentUserId = userCheckPort.getIdByEmail(email);

        PoolUpdate updateDomain = poolUpdateRestMapper.toDomain(request);
        PoolUpdate created = poolUpdateService.createUpdate(poolId, updateDomain, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(poolUpdateRestMapper.toResponse(created));
    }

    @GetMapping
    @Operation(summary = "Récupérer la timeline d'actualités d'une cagnotte")
    public ResponseEntity<List<PoolUpdateResponse>> getUpdates(
            @PathVariable String poolId,
            @RequestParam(required = false) String userId) {
        List<PoolUpdate> updates = poolUpdateService.getUpdatesByPoolId(poolId);
        List<PoolUpdateResponse> responseList = updates.stream()
                .map(u -> {
                    long likesCount = reactionRepositoryPort.countByTarget(u.getId(), "UPDATE");
                    boolean userLiked = false;
                    if (userId != null && !userId.isBlank()) {
                        userLiked = reactionRepositoryPort.findByTarget(u.getId(), "UPDATE")
                                .stream()
                                .anyMatch(r -> userId.equals(r.getUserId()));
                    }
                    return new PoolUpdateResponse(
                            u.getId(),
                            u.getPoolId(),
                            u.getTitle(),
                            u.getContent(),
                            u.getImageUrl(),
                            u.getVideoUrl(),
                            u.getCreatedAt(),
                            u.getUpdatedAt(),
                            likesCount,
                            userLiked
                    );
                })
                .toList();
        return ResponseEntity.ok(responseList);
    }
}
