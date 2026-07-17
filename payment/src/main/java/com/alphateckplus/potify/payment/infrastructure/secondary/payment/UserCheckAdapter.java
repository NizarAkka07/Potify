package com.alphateckplus.potify.payment.infrastructure.secondary.payment;

import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.payment.application_service.secondary.payment.UserCheckPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCheckAdapter implements UserCheckPort {

    private final UserEntityRepository userEntityRepository;

    @Override
    public String getEmailById(String userId) {
        return userEntityRepository.findById(userId)
                .map(user -> user.getEmail())
                .orElse(null);
    }

    @Override
    public String getIdByEmail(String email) {
        return userEntityRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElse(null);
    }
}
