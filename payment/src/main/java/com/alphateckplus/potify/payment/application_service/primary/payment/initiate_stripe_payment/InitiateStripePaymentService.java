package com.alphateckplus.potify.payment.application_service.primary.payment.initiate_stripe_payment;

import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutRequest;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutResponse;

public interface InitiateStripePaymentService {
    CheckoutResponse execute(CheckoutRequest request) throws Exception;
}
