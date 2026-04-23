package com.alphateckplus.potify.user.domain.exception;

/**
 * Exception metier levee si la permission existe deja.
 */
public class PermissionAlreadyExistsException extends RuntimeException {

    public PermissionAlreadyExistsException(String permissionCode) {
        super("Une permission existe deja avec le code: " + permissionCode);
    }
}
