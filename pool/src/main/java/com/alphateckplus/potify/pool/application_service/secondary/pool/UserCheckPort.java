package com.alphateckplus.potify.pool.application_service.secondary.pool;

public interface UserCheckPort {
    boolean existsByEmail(String email);
    String getEmailById(String userId);
}
