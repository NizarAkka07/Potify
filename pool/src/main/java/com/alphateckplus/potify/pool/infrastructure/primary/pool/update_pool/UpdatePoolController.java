package com.alphateckplus.potify.pool.infrastructure.primary.pool.update_pool;

import com.alphateckplus.potify.pool.application_service.primary.pool.update_pool.UpdatePoolService;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.UpdatePoolRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper.PoolRestMapper;
import com.alphateckplus.potify.pool.infrastructure.secondary.cloudinary.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Management")
public class UpdatePoolController {

    private final UpdatePoolService updatePoolService;
    private final PoolRestMapper poolRestMapper;
    private final CloudinaryService cloudinaryService;

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mettre a jour une cagnotte")
    public ResponseEntity<PoolResponse> updatePool(@PathVariable String id, @Valid @RequestBody UpdatePoolRequest request) {
        Pool poolToUpdate = poolRestMapper.toDomain(id, request);

        if (poolToUpdate.getImageContent() != null && poolToUpdate.getImageContent().length > 0) {
            try {
                String cloudinaryUrl = cloudinaryService.uploadImage(poolToUpdate.getImageContent());
                poolToUpdate.setImageUrl(cloudinaryUrl);
                // Vider les octets locaux pour économiser la base de données
                poolToUpdate.setImageContent(new byte[0]);
                poolToUpdate.setImageContentType("");
            } catch (Exception e) {
                // Log or handle silently
            }
        }

        Pool updatedPool = updatePoolService.execute(poolToUpdate);
        return ResponseEntity.ok(poolRestMapper.toResponse(updatedPool));
    }
}
