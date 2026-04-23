package com.alphateckplus.potify.user.domain.exception;

/**
 * Exception metier levee si un email est deja utilise.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super("Un utilisateur existe deja avec l'email: " + email);
    }
}
