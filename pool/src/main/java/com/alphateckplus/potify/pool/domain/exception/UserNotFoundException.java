package com.alphateckplus.potify.pool.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("L'utilisateur avec l'email " + email + " n'existe pas dans Potify.");
    }
}
