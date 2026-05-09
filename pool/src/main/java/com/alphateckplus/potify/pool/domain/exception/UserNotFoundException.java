package com.alphateckplus.potify.pool.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("L'utilisateur avec l'email " + email + " n'existe pas dans Potify.");
    }
}
