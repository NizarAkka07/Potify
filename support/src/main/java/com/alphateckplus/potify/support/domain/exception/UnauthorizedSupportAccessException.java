package com.alphateckplus.potify.support.domain.exception;

public class UnauthorizedSupportAccessException extends RuntimeException {

    public UnauthorizedSupportAccessException(String message) {
        super(message);
    }
}
