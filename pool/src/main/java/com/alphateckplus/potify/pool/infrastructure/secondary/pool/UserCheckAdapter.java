package com.alphateckplus.potify.pool.infrastructure.secondary.pool;

import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.pool.application_service.secondary.pool.UserCheckPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCheckAdapter implements UserCheckPort {

    private final UserEntityRepository userEntityRepository;

    @Override
    public boolean existsByEmail(String email) {
        return userEntityRepository.existsByEmail(email);
    }

    @Override
    public String getEmailById(String userId) {
        return userEntityRepository.findById(userId)
                .map(user -> user.getEmail())
                .orElse(null);
    }
}
