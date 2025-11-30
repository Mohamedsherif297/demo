package com.mealplanner.api.exception;

import com.mealplanner.api.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                "The requested resource could not be found. Please verify the ID or parameters and try again.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(
            ValidationException ex, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                ex.getMessage(),
                "The request data failed validation. Please check the input fields and ensure all required data is provided in the correct format.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(
            UnauthorizedException ex, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                "Authentication is required to access this resource. Please log in with valid credentials or provide a valid access token.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> handleForbidden(
            ForbiddenException ex, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                "You do not have permission to access this resource. This action requires specific privileges or ownership.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "Access denied",
                "You do not have sufficient permissions to perform this action. Contact an administrator if you believe this is an error.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessRule(
            BusinessRuleException ex, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Business Rule Violation",
                ex.getMessage(),
                "The request violates a business rule or constraint. Please review the error message and adjust your request accordingly.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(DeliveryException.class)
    public ResponseEntity<ErrorResponseDto> handleDelivery(
            DeliveryException ex, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Delivery Error",
                ex.getMessage(),
                "The delivery operation could not be completed due to invalid state or constraints. Please check the delivery status and requirements.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleResponseStatus(
            ResponseStatusException ex, HttpServletRequest request) {
        String description = getDescriptionForStatus(ex.getStatusCode().value());
        
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason() != null ? ex.getReason() : "Request failed",
                description,
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Extract field names and error messages for validation failures
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + 
                        (error.getDefaultMessage() != null ? error.getDefaultMessage() : "validation error"))
                .collect(Collectors.joining(", "));
        
        // List missing required fields if any
        String missingFields = ex.getBindingResult().getFieldErrors().stream()
                .filter(error -> {
                    String defaultMessage = error.getDefaultMessage();
                    return defaultMessage != null && defaultMessage.contains("required");
                })
                .map(error -> error.getField())
                .collect(Collectors.joining(", "));
        
        String finalMessage = missingFields.isEmpty() 
                ? "Validation failed: " + message
                : "Validation failed: " + message;
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                finalMessage,
                "One or more fields in the request body failed validation. Please ensure all required fields are provided with valid values.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        Class<?> requiredType = ex.getRequiredType();
        String typeName = requiredType != null ? requiredType.getSimpleName() : "unknown";
        String message = String.format("Invalid value for parameter '%s': expected type %s", 
                ex.getName(), 
                typeName);
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Parameter Type",
                message,
                "A parameter in the request has an incorrect data type. Please check the API documentation for the expected parameter types.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Invalid request body: malformed JSON or invalid data types";
        
        // Try to extract more specific information without exposing internal details
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("JSON parse error")) {
                message = "Invalid JSON format in request body";
            } else if (ex.getMessage().contains("Cannot deserialize")) {
                message = "Invalid data type in request body";
            }
        }
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request Body",
                message,
                "The request body could not be parsed. Please ensure the JSON is properly formatted and all fields have the correct data types.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        // Prevent exposure of internal database details
        String message = "Data integrity constraint violation";
        
        // Provide user-friendly messages for common constraint violations
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("foreign key constraint")) {
                message = "Referenced entity does not exist";
            } else if (ex.getMessage().contains("unique constraint") || 
                       ex.getMessage().contains("duplicate key")) {
                message = "Duplicate entry: this record already exists";
            } else if (ex.getMessage().contains("not-null constraint")) {
                message = "Required field is missing";
            }
        }
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Data Integrity Error",
                message,
                "The operation violates a database constraint. This could be due to duplicate entries, missing required fields, or invalid references.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneral(
            Exception ex, HttpServletRequest request) {
        // Log the actual error for debugging
        System.err.println("=== UNHANDLED EXCEPTION ===");
        System.err.println("Type: " + ex.getClass().getName());
        System.err.println("Message: " + ex.getMessage());
        System.err.println("Path: " + request.getRequestURI());
        ex.printStackTrace();
        
        // Prevent exposure of internal details in error messages
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred while processing your request",
                "The server encountered an unexpected condition. Please try again later or contact support if the problem persists.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Helper method to provide descriptions based on status code
    private String getDescriptionForStatus(int statusCode) {
        return switch (statusCode) {
            case 400 -> "The request contains invalid data or is malformed. Please check your input and try again.";
            case 401 -> "Authentication is required. Please provide valid credentials or a valid access token.";
            case 403 -> "You do not have permission to access this resource. Contact an administrator if needed.";
            case 404 -> "The requested resource was not found. Please verify the URL and parameters.";
            case 409 -> "The request conflicts with the current state of the resource. This may be due to duplicate data.";
            case 422 -> "The request is well-formed but contains semantic errors. Please review the business rules.";
            case 500 -> "An internal server error occurred. Please try again later or contact support.";
            default -> "An error occurred while processing your request. Please try again.";
        };
    }
}
