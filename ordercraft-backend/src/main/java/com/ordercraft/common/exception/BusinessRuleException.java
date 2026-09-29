package com.ordercraft.common.exception;

/**
 * Thrown when a business rule is violated (e.g., invalid state transitions).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
