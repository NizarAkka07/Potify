package com.alphateckplus.potify.payment.infrastructure.primary.payment.get_pool_contributions;

import com.alphateckplus.potify.payment.application_service.primary.payment.get_pool_contributions.GetPoolContributionsService;
import com.alphateckplus.potify.payment.domain.model.Contribution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GetPoolContributionsController {

    private final GetPoolContributionsService useCase;

    @GetMapping("/api/contributions/pool/{poolId}")
    public ResponseEntity<List<Contribution>> handle(@PathVariable String poolId) {
        try {
            List<Contribution> list = useCase.execute(poolId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("Error fetching pool contributions", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
