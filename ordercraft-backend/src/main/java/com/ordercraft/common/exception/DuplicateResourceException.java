package com.ordercraft.common.exception;

/**
 * Thrown when attempting to create a resource that violates a uniqueness constraint.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String entityName, String field, Object value) {
        super(String.format("%s already exists with %s: %s", entityName, field, value));
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
