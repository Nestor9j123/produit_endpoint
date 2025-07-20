package com.dailycodebuffer.security.exception;

/**
 * Exception personnalisée pour les erreurs liées aux tokens JWT
 */
public class JwtException extends RuntimeException {
    
    public JwtException(String message) {
        super(message);
    }
    
    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
} 