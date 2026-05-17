package com.alphateckplus.potify.pool.infrastructure.primary;

import com.alphateckplus.potify.pool.domain.exception.PoolNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour le microservice pool.
 */
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(PoolNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePoolNotFound(PoolNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(com.alphateckplus.potify.pool.domain.exception.UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(com.alphateckplus.potify.pool.domain.exception.UserNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
