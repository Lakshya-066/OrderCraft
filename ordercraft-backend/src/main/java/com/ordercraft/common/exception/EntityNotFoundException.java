package com.ordercraft.common.exception;

/**
 * Thrown when an entity is not found by ID or other unique identifier.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Object identifier) {
        super(String.format("%s not found with identifier: %s", entityName, identifier));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
