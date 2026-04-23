package com.alphateckplus.potify.user.domain.exception;

/**
 * Exception metier levee si le role n'existe pas.
 */
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String roleId) {
        super("Role introuvable pour l'id: " + roleId);
    }
}
