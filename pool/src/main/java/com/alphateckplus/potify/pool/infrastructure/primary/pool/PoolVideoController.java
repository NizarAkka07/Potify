package com.alphateckplus.potify.pool.infrastructure.primary.pool;

import com.alphateckplus.potify.pool.application_service.primary.command.UpdatePoolCommand;
import com.alphateckplus.potify.pool.application_service.primary.pool.get_pool.GetPoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.update_pool.UpdatePoolService;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.infrastructure.secondary.cloudinary.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Video Management")
public class PoolVideoController {

    private final GetPoolService getPoolService;
    private final UpdatePoolService updatePoolService;
    private final CloudinaryService cloudinaryService;

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

    @PostMapping(value = "/{id}/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploader une vidéo pour une cagnotte sur Cloudinary")
    public ResponseEntity<Void> uploadPoolVideo(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 1. Envoyer le fichier sur Cloudinary de manière sécurisée sans erreur de CORS
        String cloudinaryUrl = cloudinaryService.uploadVideo(file);

        // 2. Mettre à jour la base de données avec l'URL Cloudinary reçue
        UpdatePoolCommand command = UpdatePoolCommand.builder()
                .id(id)
                .videoUrl(cloudinaryUrl)
                // On s'assure qu'on n'a pas de contenu binaire en local pour économiser la base de données
                .videoContent(new byte[0])
                .videoContentType("")
                .build();

        updatePoolService.execute(command);

        return ResponseEntity.ok().build();
    }
}
