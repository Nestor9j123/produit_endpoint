package com.dailycodebuffer.security.exception;

/**
 * Exception personnalisée pour les conflits de ressources (ressource déjà existante)
 */
public class ResourceConflictException extends RuntimeException {
    
    public ResourceConflictException(String message) {
        super(message);
    }
    
    public ResourceConflictException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s avec %s '%s' existe déjà", resourceName, fieldName, fieldValue));
    }
} 