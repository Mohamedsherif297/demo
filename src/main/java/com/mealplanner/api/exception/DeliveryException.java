package com.mealplanner.api.exception;

/**
 * Custom exception for delivery-specific operations.
 * Used when delivery operations fail due to business rules or state constraints.
 */
public class DeliveryException extends RuntimeException {
    public DeliveryException(String message) {
        super(message);
    }
    
    public DeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
