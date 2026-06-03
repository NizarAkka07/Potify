package com.alphateckplus.potify.payment.application_service.primary.payment.initiate_paypal_payment;

import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutRequest;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutResponse;

public interface InitiatePayPalPaymentService {
    CheckoutResponse execute(CheckoutRequest request) throws Exception;
}
