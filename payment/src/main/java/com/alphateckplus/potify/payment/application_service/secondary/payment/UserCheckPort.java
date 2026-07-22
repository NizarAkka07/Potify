package com.alphateckplus.potify.payment.application_service.secondary.payment;

public interface UserCheckPort {
    String getEmailById(String userId);
    String getIdByEmail(String email);
    boolean isAdminVerified(String userId);
}
