package com.alphateckplus.potify.payment.application_service.primary.payment.withdraw;

public interface ConfirmWithdrawalService {
    void execute(String transactionId) throws Exception;
}
