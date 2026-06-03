package com.alphateckplus.potify.payment.application_service.primary.payment.confirm_paypal_payment;

public interface ConfirmPayPalPaymentService {
    void execute(String orderId) throws Exception;
}
