package com.alphateckplus.potify.payment.infrastructure.primary.payment.get_user_contributions;

import com.alphateckplus.potify.payment.application_service.primary.payment.get_user_contributions.GetUserContributionsService;
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
public class GetUserContributionsController {

    private final GetUserContributionsService useCase;

    @GetMapping("/api/contributions/user/{userId}")
    public ResponseEntity<List<Contribution>> handle(@PathVariable String userId) {
        try {
            List<Contribution> list = useCase.execute(userId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("Error fetching user contributions", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
