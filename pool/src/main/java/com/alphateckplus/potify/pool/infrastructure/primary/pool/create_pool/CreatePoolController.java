package com.alphateckplus.potify.pool.infrastructure.primary.pool.create_pool;

import com.alphateckplus.potify.pool.application_service.primary.pool.create_pool.CreatePoolService;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.CreatePoolRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.dto.PoolResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.mapper.PoolRestMapper;
import com.alphateckplus.potify.pool.infrastructure.secondary.cloudinary.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pool Management")
public class CreatePoolController {

    private final CreatePoolService createPoolService;
    private final PoolRestMapper poolRestMapper;
    private final CloudinaryService cloudinaryService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Creer une nouvelle cagnotte")
    public ResponseEntity<PoolResponse> createPool(@Valid @RequestBody CreatePoolRequest request) {
        Pool poolToCreate = poolRestMapper.toDomain(request);

        if (poolToCreate.getImageContent() != null && poolToCreate.getImageContent().length > 0) {
            try {
                String cloudinaryUrl = cloudinaryService.uploadImage(poolToCreate.getImageContent());
                poolToCreate.setImageUrl(cloudinaryUrl);
                // Vider les octets locaux pour économiser la base de données
                poolToCreate.setImageContent(new byte[0]);
                poolToCreate.setImageContentType("");
            } catch (Exception e) {
                // Log or handle silently
            }
        }

        Pool createdPool = createPoolService.execute(poolToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(poolRestMapper.toResponse(createdPool));
    }
}
