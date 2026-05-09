package com.alphateckplus.potify.user.application_service.primary.user.verify_email;

import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.UserStatus;

import java.util.Optional;

public class DefaultVerifyEmailService implements VerifyEmailService {

    private final UserRepositoryPort userRepositoryPort;

    public DefaultVerifyEmailService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public boolean execute(String token) {
        Optional<User> userOpt = userRepositoryPort.findByVerificationToken(token);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(UserStatus.ACTIVE);
            user.setEnabled(true);
            user.setVerificationToken(null);
            userRepositoryPort.save(user);
            return true;
        }
        
        return false;
    }
}
