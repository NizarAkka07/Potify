package com.alphateckplus.potify.payment.infrastructure.primary.payment.initiate_paypal_payment;

import com.alphateckplus.potify.payment.application_service.primary.payment.initiate_paypal_payment.InitiatePayPalPaymentService;
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
public class InitiatePayPalPaymentController {

    private final InitiatePayPalPaymentService useCase;

    @lombok.Value
    public static class ErrorResponse {
        String message;
    }

    @PostMapping("/api/payments/checkout/paypal")
    public ResponseEntity<?> handle(@RequestBody CheckoutRequest request) {
        try {
            CheckoutResponse response = useCase.execute(request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("PayPal initiation validation error: {}", e.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("PayPal initiation error", e);
            String msg = e.getMessage();
            if (msg != null && msg.contains("401")) {
                msg = "Authentification PayPal échouée. Veuillez vérifier le client-id et le secret de test dans application.yml";
            }
            return ResponseEntity.status(500).body(new ErrorResponse(msg));
        }
    }
}
