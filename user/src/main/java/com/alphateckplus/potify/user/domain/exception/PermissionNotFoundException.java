package com.alphateckplus.potify.user.domain.exception;

/**
 * Exception metier levee si la permission n'existe pas.
 */
public class PermissionNotFoundException extends RuntimeException {

    public PermissionNotFoundException(String permissionId) {
        super("Permission introuvable pour l'id: " + permissionId);
    }
}
