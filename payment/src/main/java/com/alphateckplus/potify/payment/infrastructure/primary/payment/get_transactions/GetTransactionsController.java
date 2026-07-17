package com.alphateckplus.potify.payment.infrastructure.primary.payment.get_transactions;

import com.alphateckplus.potify.payment.application_service.primary.payment.get_transactions.GetTransactionsService;
import com.alphateckplus.potify.payment.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GetTransactionsController {

    private final GetTransactionsService useCase;

    @GetMapping("/api/payments/transactions")
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public ResponseEntity<List<Transaction>> handle() {
        try {
            return ResponseEntity.ok(useCase.execute());
        } catch (Exception e) {
            log.error("Error fetching transactions", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
