package com.alphateckplus.potify.pool.infrastructure.primary.image.upload_image;

import com.alphateckplus.potify.pool.infrastructure.secondary.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class UploadImageController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Fichier vide"));
        }

        try {
            String fileUrl = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erreur lors de l'upload: " + e.getMessage()));
        }
    }
}
