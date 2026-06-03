package com.alphateckplus.potify.payment.infrastructure.primary.payment.initiate_stripe_payment;

import com.alphateckplus.potify.payment.application_service.primary.payment.initiate_stripe_payment.InitiateStripePaymentService;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutRequest;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InitiateStripePaymentController {

    private final InitiateStripePaymentService useCase;

    @lombok.Value
    public static class ErrorResponse {
        String message;
    }

    @PostMapping("/api/payments/checkout/stripe")
    public ResponseEntity<?> handle(@RequestBody CheckoutRequest request) {
        try {
            CheckoutResponse response = useCase.execute(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Stripe initiation error", e);
            String msg = e.getMessage();
            if (msg != null && msg.contains("sk_test")) {
                msg = "Clé de test Stripe invalide ou non configurée dans application.yml";
            }
            return ResponseEntity.status(500).body(new ErrorResponse(msg));
        }
    }
}
