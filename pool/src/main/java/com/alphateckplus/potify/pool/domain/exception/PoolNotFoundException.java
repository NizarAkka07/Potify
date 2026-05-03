package com.alphateckplus.potify.pool.domain.exception;

/**
 * Exception levee quand une cagnotte n'est pas trouvee dans le domaine.
 */
public class PoolNotFoundException extends RuntimeException {
    public PoolNotFoundException(String message) {
        super(message);
    }
}
