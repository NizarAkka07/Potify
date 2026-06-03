package com.alphateckplus.potify.payment.infrastructure.primary.payment.confirm_stripe_payment;

import com.alphateckplus.potify.payment.application_service.primary.payment.confirm_stripe_payment.ConfirmStripePaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConfirmStripePaymentController {

    private final ConfirmStripePaymentService useCase;

    @lombok.Value
    public static class ErrorResponse {
        String message;
    }

    @GetMapping("/api/payments/confirm/stripe")
    public ResponseEntity<?> handle(@RequestParam("session_id") String sessionId) {
        try {
            useCase.execute(sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Stripe confirmation error", e);
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }
}
