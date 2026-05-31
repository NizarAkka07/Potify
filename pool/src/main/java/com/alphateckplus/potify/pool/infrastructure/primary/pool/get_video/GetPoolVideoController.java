package com.alphateckplus.potify.pool.infrastructure.primary.pool.get_video;

import com.alphateckplus.potify.pool.application_service.primary.pool.get_pool.GetPoolService;
import com.alphateckplus.potify.pool.domain.model.Pool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Video Management")
public class GetPoolVideoController {

    private final GetPoolService getPoolService;

    @GetMapping("/{id}/video")
    @Operation(summary = "Récupérer la vidéo d'une cagnotte depuis la base de données")
    public ResponseEntity<byte[]> getPoolVideo(@PathVariable String id) {
        Pool pool = getPoolService.execute(id)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte non trouvée : " + id));
        
        if (pool.getVideoContent() == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = pool.getVideoContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "video/mp4"; // Par défaut
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(pool.getVideoContent());
    }
}
