package com.alphateckplus.potify.user.domain.exception;

/**
 * Exception metier levee si l'utilisateur n'existe pas.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("Utilisateur introuvable pour l'id: " + userId);
    }
}
