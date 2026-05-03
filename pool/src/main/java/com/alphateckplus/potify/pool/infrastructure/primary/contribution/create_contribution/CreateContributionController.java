package com.alphateckplus.potify.pool.infrastructure.primary.contribution.create_contribution;

import com.alphateckplus.potify.pool.application_service.primary.contribution.create_contribution.CreateContributionService;
import com.alphateckplus.potify.pool.domain.model.Contribution;
import com.alphateckplus.potify.pool.infrastructure.primary.contribution.dto.CreateContributionRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.contribution.dto.ContributionResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.contribution.mapper.ContributionRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contributions")
@RequiredArgsConstructor
@Tag(name = "Contribution Management")
public class CreateContributionController {

    private final CreateContributionService createContributionService;
    private final ContributionRestMapper contributionRestMapper;

    @PostMapping
    @Operation(summary = "Effectuer une nouvelle contribution")
    public ResponseEntity<ContributionResponse> createContribution(@Valid @RequestBody CreateContributionRequest request) {
        Contribution created = createContributionService.execute(contributionRestMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(contributionRestMapper.toResponse(created));
    }
}
