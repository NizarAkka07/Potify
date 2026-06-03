package com.alphateckplus.potify.payment.application_service.primary.payment.confirm_stripe_payment;

public interface ConfirmStripePaymentService {
    void execute(String sessionId) throws Exception;
}
