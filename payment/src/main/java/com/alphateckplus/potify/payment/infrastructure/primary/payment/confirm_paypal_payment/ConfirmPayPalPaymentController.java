package com.alphateckplus.potify.payment.infrastructure.primary.payment.confirm_paypal_payment;

import com.alphateckplus.potify.payment.application_service.primary.payment.confirm_paypal_payment.ConfirmPayPalPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConfirmPayPalPaymentController {

    private final ConfirmPayPalPaymentService useCase;

    @lombok.Value
    public static class ErrorResponse {
        String message;
    }

    @GetMapping("/api/payments/confirm/paypal")
    public ResponseEntity<?> handle(@RequestParam("token") String orderId) {
        try {
            useCase.execute(orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("PayPal confirmation error", e);
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }
}
