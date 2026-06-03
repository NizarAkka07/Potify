package com.alphateckplus.potify.payment.application_service.secondary.payment;

import com.alphateckplus.potify.payment.domain.model.Contribution;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutResponse;

public interface PayPalGatewayPort {
    CheckoutResponse initiatePayPalPayment(Contribution contribution, String cancelUrl) throws Exception;
    void confirmPayPalPayment(String orderId) throws Exception;
}
