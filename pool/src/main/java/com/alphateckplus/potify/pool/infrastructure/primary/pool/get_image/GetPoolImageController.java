package com.alphateckplus.potify.pool.infrastructure.primary.pool.get_image;

import com.alphateckplus.potify.pool.application_service.primary.pool.get_pool.GetPoolService;
import com.alphateckplus.potify.pool.domain.model.Pool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Image Management")
public class GetPoolImageController {

    private final GetPoolService getPoolService;

    @GetMapping("/{id}/image")
    @Operation(summary = "Récupérer l'image d'une cagnotte depuis la base de données")
    public ResponseEntity<byte[]> getPoolImage(@PathVariable String id) {
        Pool pool = getPoolService.execute(id)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte non trouvée : " + id));
        
        if (pool.getImageContent() == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = pool.getImageContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "image/jpeg"; // Par défaut
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(pool.getImageContent());
    }
}
