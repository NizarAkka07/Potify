package com.alphateckplus.potify.payment.application_service.primary.payment.withdraw;

public interface WithdrawService {
    void execute(WithdrawRequest request) throws Exception;
}
