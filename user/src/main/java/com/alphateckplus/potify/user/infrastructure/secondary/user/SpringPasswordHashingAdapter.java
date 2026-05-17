package com.alphateckplus.potify.user.infrastructure.secondary.user;

import com.alphateckplus.potify.user.application_service.secondary.user.PasswordHashingPort;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Adaptateur implémentant le hachage de mot de passe via Spring Security.
 */
public class SpringPasswordHashingAdapter implements PasswordHashingPort {

    private final PasswordEncoder passwordEncoder;

    public SpringPasswordHashingAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
