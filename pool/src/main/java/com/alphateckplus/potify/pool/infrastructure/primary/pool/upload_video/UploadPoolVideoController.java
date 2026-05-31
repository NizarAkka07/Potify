package com.alphateckplus.potify.pool.infrastructure.primary.pool.upload_video;

import com.alphateckplus.potify.pool.application_service.primary.pool.update_pool.UpdatePoolService;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.infrastructure.secondary.cloudinary.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Video Management")
public class UploadPoolVideoController {

    private final UpdatePoolService updatePoolService;
    private final CloudinaryService cloudinaryService;

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
        Pool poolToUpdate = Pool.builder()
                .id(id)
                .videoUrl(cloudinaryUrl)
                // On s'assure qu'on n'a pas de contenu binaire en local pour économiser la base de données
                .videoContent(new byte[0])
                .videoContentType("")
                .build();

        updatePoolService.execute(poolToUpdate);

        return ResponseEntity.ok().build();
    }
}
