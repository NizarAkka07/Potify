package com.alphateckplus.potify.payment.infrastructure.primary.payment.withdraw;

import com.alphateckplus.potify.payment.application_service.primary.payment.withdraw.WithdrawRequest;
import com.alphateckplus.potify.payment.application_service.primary.payment.withdraw.WithdrawService;
import com.alphateckplus.potify.payment.application_service.primary.payment.withdraw.ConfirmWithdrawalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WithdrawController {

    private final WithdrawService useCase;
    private final ConfirmWithdrawalService confirmUseCase;

    @lombok.Value
    public static class ErrorResponse {
        String message;
    }

    @PostMapping("/api/payments/withdraw")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> handle(@RequestBody WithdrawRequest request) {
        try {
            useCase.execute(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.warn("Withdrawal validation warning: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Withdrawal error", e);
            return ResponseEntity.status(500).body(new ErrorResponse("Une erreur interne est survenue lors du retrait."));
        }
    }

    @PostMapping("/api/payments/withdraw/{transactionId}/confirm")
    @PreAuthorize("hasAuthority('PAYMENT_CONFIRM') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> confirm(@PathVariable String transactionId) {
        try {
            confirmUseCase.execute(transactionId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Withdrawal confirmation warning: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Withdrawal confirmation error", e);
            return ResponseEntity.status(500).body(new ErrorResponse("Une erreur interne est survenue lors de la confirmation du retrait."));
        }
    }
}
