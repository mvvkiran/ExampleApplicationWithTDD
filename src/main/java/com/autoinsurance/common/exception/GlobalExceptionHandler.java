package com.autoinsurance.common.exception;

import com.autoinsurance.quote.exception.InvalidQuoteRequestException;
import com.autoinsurance.quote.exception.QuoteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidQuoteRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidQuoteRequest(InvalidQuoteRequestException ex) {
        log.warn("Invalid quote request: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message", ex.getMessage(),
                        "errorCode", "INVALID_QUOTE_REQUEST",
                        "timestamp", Instant.now().toEpochMilli()
                ));
    }

    @ExceptionHandler(QuoteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleQuoteNotFound(QuoteNotFoundException ex) {
        log.warn("Quote not found: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "message", ex.getMessage(),
                        "errorCode", "QUOTE_NOT_FOUND",
                        "timestamp", Instant.now().toEpochMilli()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message", errorMessage.toString(),
                        "errorCode", "VALIDATION_ERROR",
                        "timestamp", Instant.now().toEpochMilli()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message", "An unexpected error occurred",
                        "errorCode", "INTERNAL_ERROR",
                        "timestamp", Instant.now().toEpochMilli()
                ));
    }
}