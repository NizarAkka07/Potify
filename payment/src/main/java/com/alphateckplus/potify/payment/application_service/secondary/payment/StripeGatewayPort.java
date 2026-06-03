package com.alphateckplus.potify.payment.application_service.secondary.payment;

import com.alphateckplus.potify.payment.domain.model.Contribution;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutResponse;

public interface StripeGatewayPort {
    CheckoutResponse initiateStripePayment(Contribution contribution, String cancelUrl) throws Exception;
    void confirmStripePayment(String sessionId) throws Exception;
}
