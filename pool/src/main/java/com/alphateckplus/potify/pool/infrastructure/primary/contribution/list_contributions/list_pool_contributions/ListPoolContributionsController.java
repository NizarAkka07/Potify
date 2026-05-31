package com.alphateckplus.potify.pool.infrastructure.primary.contribution.list_contributions.list_pool_contributions;

import com.alphateckplus.potify.pool.application_service.primary.contribution.list_contributions.ListContributionsService;
import com.alphateckplus.potify.pool.infrastructure.primary.contribution.dto.ContributionResponse;
import com.alphateckplus.potify.pool.infrastructure.primary.contribution.mapper.ContributionRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/contributions")
@RequiredArgsConstructor
@Tag(name = "Contribution Management")
public class ListPoolContributionsController {

    private final ListContributionsService listContributionsService;
    private final ContributionRestMapper mapper;

    @GetMapping("/pool/{poolId}")
    @Operation(summary = "Lister les contributions pour une cagnotte")
    public ResponseEntity<List<ContributionResponse>> listPoolContributions(@PathVariable String poolId) {
        List<ContributionResponse> contributions = listContributionsService.findByPoolId(poolId).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(contributions);
    }
}
