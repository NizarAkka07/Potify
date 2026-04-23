package com.alphateckplus.potify.user.domain.exception;

/**
 * Exception metier levee si le role existe deja.
 */
public class RoleAlreadyExistsException extends RuntimeException {

    public RoleAlreadyExistsException(String roleName) {
        super("Un role existe deja avec le nom: " + roleName);
    }
}
